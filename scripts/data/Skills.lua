local GATHER_SKILL_BASE_DURATION = 12000


---@type table<number, fun(p:Player, cellId:number)>
SKILLS = {}

---@type table<number, number>
SKILL_JOBS = {}

---@class RecipeDef
---@field item number
---@field ingredients ItemStack[]


---@type table<number, RecipeDef[]>
SKILL_CRAFTS = {}

---@class SkillRequirements
---@field jobID number
---@field jobLvl number
---@field toolIDs number[]
---@field toolType number

---@class GatherJobSkillDef
---@field id number
---@field obj InteractiveObjectDef
---@field minLvl number
---@field itemID number
---@field rewardFn fun(p):table{number, ItemStack}
---@field xp number
---@field respawn number[2]

---@param p Player
---@param requirements SkillRequirements
---@return boolean passed
local function checkRequirements(p, requirements)
    if not requirements then return true end

    local jobLvl = 0
    if requirements.jobID then
        jobLvl = p:jobLevel(requirements.jobID)
        -- Doesn't know the job
        -- TODO: Chat message ?
        if jobLvl == 0 then
            print("PLAYER DOESN'T KNOW JOB")
            return false end
    end

    if requirements.jobLvl and jobLvl < requirements.jobLvl then
        print("PLAYER TOO LOW LEVEL AT JOB")
        -- Not high level enough
        -- TODO: Chat message ?
        return false
    end

    local tool = p:gearAt(WeaponSlot)
    if requirements.toolIDs and not table.contains(requirements.toolIDs, tool:id()) then
        print("WRONG TOOL FOR JOB")
        -- Wrong tool
        -- TODO: Chat message ?
        return false
    end

    if requirements.toolType and tool:type() ~= requirements.toolType then
        print("WRONG TOOL TYPE FOR JOB")
        -- Wrong tool
        -- TODO: Chat message ?
        return false
    end

    return true
end

---@alias GatherRewardFn fun(p:Player)
---@alias GatherDurationFn fun(p:Player):number

---@param skillID number
---@param crafts RecipeDef[]
---@param requirements SkillRequirements
---@param ingredientCountFn fun(p:Player):number
---@param jobID number
function registerCraftSkill(skillID, crafts, requirements, ingredientCountFn, jobID)
    SKILLS[skillID] = function(p, cellId)
        if not checkRequirements(p, requirements) then return end

        -- Animation
        local map = p:map()

        if map:getAnimationState(cellId) == AnimStates.READY then
            -- Official: sends LOCKED(F=2) then IN_USE (F=3) in the same packet
            -- Official: sends READYING(F=5) after ~55 seconds

            -- map:setAnimationState(cellId, AnimStates.LOCKED)
            map:setAnimationState(cellId, AnimStates.IN_USE)
            World:delayForMs(55000, function()
                map:setAnimationState(cellId, AnimStates.READYING)
            end)
        end

        return p:useCraftSkill(skillID, ingredientCountFn(p))
    end

    -- Convert {id, qua} to ItemStack and sort by templateID
    for _, craft in ipairs(crafts) do
        for i, ingredient in ipairs(craft.ingredients) do
            craft.ingredients[i] = ItemStack(ingredient[1], ingredient[2])
        end
        table.sort(craft.ingredients, ItemStack.itemIDASC)
    end
    SKILL_CRAFTS[skillID] = crafts

    if jobID then
        SKILL_JOBS[skillID] = jobID
    end

end

---@param minTime number
---@param maxTime number
---@return fun():number
function respawnBetweenMillis(minTime, maxTime)
    return function()
        return math.random(minTime, maxTime)
    end
end

---@param skillId number
---@param durationFn GatherDurationFn
---@param rewardFn GatherRewardFn
---@param respawnIntervalFn fun():number
---@param requirements SkillRequirements
function registerGatherSkill(skillId, actorAnimID, durationFn, rewardFn, respawnIntervalFn, requirements)
    ---@param p Player
    SKILLS[skillId] = function(p, cellId)
        if not checkRequirements(p, requirements) then return end

        local map = p:map()
        if map:getAnimationState(cellId) ~= AnimStates.READY then
            print("OBJECT NOT READY")
            -- TODO: Chat message ?
            return
        end

        map:setAnimationState(cellId, AnimStates.LOCKED)
        p:setExchangeAction(UsingObjectAction)

        local duration = durationFn(p)

        -- Actor animation
        local actionParams = tostring(cellId)..","..tostring(duration)
        if actorAnimID then
            actionParams = actionParams .. "," .. tostring(actorAnimID)
        end
        p:map():sendAction(p, 0, 501, actionParams)

        World:delayForMs(duration, function()
            -- Done Gathering, reward
            rewardFn(p)

            -- animate object
            map:setAnimationState(cellId, AnimStates.IN_USE)

            p:clearExchangeAction(UsingObjectAction)
            -- Respawn
            if not respawnIntervalFn then return end

            local respawnDelay = respawnIntervalFn()
            World:delayForMs(respawnDelay, function()
                if map:getAnimationState(cellId) ~= AnimStates.NOT_READY then return end

                map:setAnimationState(cellId, AnimStates.READYING)
            end)
        end)
    end
end

---@param p Player
---@param itemID number
---@param quantity number
function gatherSkillAddItem(p, itemID, quantity)
    p:addItem(itemID, quantity)
    p:showReceivedItem(p:id(), quantity)
end

---@param jobID number
---@param toolInfo table<>
---@param skills GatherJobSkillDef[]
function registerGatherJobSkills(jobID, toolInfo, skills)
    for _, sk in pairs(skills) do
        local durationForPlayer = function(p)
            local lvlDiff = p:jobLevel(jobID) - sk.minLvl
            return GATHER_SKILL_BASE_DURATION - 100 * lvlDiff
        end

        ---@param p Player
        local rewardFn = function(p)
            local lvlDiff = p:jobLevel(jobID) - sk.minLvl
            local quantity = math.random(1, 2 + math.floor(lvlDiff / 5))

            local itemID = sk.itemID
            local xp = sk.xp
            if sk.rewardFn then
                local rXp, rStack = sk:rewardFn(p)
                xp = rXp
                itemID = rStack.itemID
                quantity = rStack.quantity
            end

            gatherSkillAddItem(p, itemID, quantity)
            if xp >0 then p:addJobXP(jobID, xp) end
        end


        local req = {jobID = jobID, jobLvl =  sk.minLvl}
        if toolInfo.toolType then req.toolType = toolInfo.toolType end
        if toolInfo.toolID then req.toolID = toolInfo.toolID end

        registerGatherSkill(
            sk.id,
            nil,
            durationForPlayer,
            rewardFn,
            respawnBetweenMillis(sk.respawn[1], sk.respawn[2]),
            req
        )
    end
end

---@param jobID number
function ingredientsForCraftJob(jobID)
    ---@param p Player
    return function(p)
        local lvl = p:jobLevel(jobID)

        if lvl == 100 then return 9
        elseif lvl < 10 then return 2
        else return lvl/20 + 4 end
    end
end

function successRateForCraftJob(jobID)
    ---@param p Player
    return function(p)
        local lvl = p:jobLevel(jobID)

        if lvl == 100 then  return 99
        elseif lvl < 10 then return 50
        else return 54 + ((lvl / 10) - 1) * 5 end
    end
end
