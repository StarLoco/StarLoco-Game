---@type table<number, fun(p:Player, cellId:number)>
SKILLS = {}

---@class SkillRequirements
---@field jobID number
---@field jobLvl number
---@field toolID number
---@field toolType number

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
        if jobLvl == 0 then return false end
    end

    if requirements.jobLvl and jobLvl < requirements.jobLvl then
        -- Not high level enough
        -- TODO: Chat message ?
        return false
    end

    local tool = p:gearAt(WeaponSlot)
    if requirements.toolID and tool:id() ~= requirements.toolID then
        -- Wrong tool
        -- TODO: Chat message ?
        return false
    end

    if requirements.toolType and tool:type() ~= requirements.toolType then
        -- Wrong tool
        -- TODO: Chat message ?
        return false
    end

    return true
end

---@alias GatherRewardFn fun(p:Player):ItemStack[]

---@param skillId number
---@param requirements SkillRequirements
function registerCraftSkill(skillId, requirements)
    SKILLS[skillId] = function(p, cellId)
        if not checkRequirements(p, requirements) then return end

        -- Animation
        local map = p:map()
        if map:getAnimationState(cellId) ~= AnimStates.IN_USE then
            map:setAnimationState(cellId, AnimStates.IN_USE)
        end

        return p:useCraftSkill(skillId, cellId)
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
---@param rewardFn GatherRewardFn
---@param respawnIntervalFn fun():number
---@param requirements SkillRequirements
function registerGatherSkill(skillId, rewardFn, respawnIntervalFn, requirements)
    ---@param p Player
    SKILLS[skillId] = function(p, cellId)
        if not checkRequirements(p, requirements) then return end

        local map = p:map()
        if map:getAnimationState(cellId) ~= AnimStates.READY then
            -- TODO: Chat message ?
            return
        end

        map:setAnimationState(cellId, AnimStates.IN_USE, function()
            local rewards = rewardFn(p)
            for _, reward in ipairs(rewards) do
                p:addItem(reward.itemID, reward.quantity)
                p:showReceivedItem(p:id(), reward.quantity)
            end

            if not respawnIntervalFn then return end

            local respawnDelay = respawnIntervalFn()
            World:delayForMs(respawnDelay, function()
                if map:getAnimationState(cellId) ~= AnimStates.NOT_READY then return end

                map:setAnimationState(cellId, AnimStates.READYING)
            end)
        end)
    end
end
