
---@param p Player
---@param cellID number
---@param skillID number
Handlers.players.onSkillUse = function(p, cellID, skillID)
    if not SKILLS[skillID] then
        JLogF("unknown skill {}", skillID)
        return
    end
    SKILLS[skillID](p, cellID)
    return
end

---@param p Player
Handlers.players.onJobSkillRequest = function(p)
    local jobInfo = {}

    for _, jobID in pairs(p:jobs()) do
        -- TODO Get skills and pickup-able resources
        jobInfo[jobID] = {}
    end

    return jobInfo
end

---@param crafts RecipeDef[]
---@param ingredients ItemStack[]
---@return number -- itemID
local findCraft = function(crafts, ingredients)
    -- TODO: Optimize using hashing functions

    if not ingredients or #ingredients == 0 then return nil end

    -- Sort offer by templateID to make things simpler
    table.sort(ingredients, ItemStack.itemIDASC)

    ---@type RecipeDef
    local match = nil
    for _, craft in ipairs(crafts) do
        (function()
            --JLogF("Testing recipe #{}", craft.item)
            -- Wrong number of provided items
            if #ingredients ~= #craft.ingredients then
                --JLogF("Wrong ingredient count")
                return -- This return actually continues the for loop
            end

            -- Check ingredient one by one
            for i in ipairs(craft.ingredients) do
                -- Wrong ingredient
                if craft.ingredients[i] ~= ingredients[i] then
                    --JLogF("Wrong ingredient idx {}", i)
                    --JLogF("{} {}", type(craft.ingredients[i]), type(ingredients[i]))
                    --JLogF("Want: {} / {}", craft.ingredients[i].itemID, craft.ingredients[i].quantity)
                    --JLogF("Gave: {} / {}", ingredients[i].itemID, ingredients[i].quantity)
                    return -- This return actually continues the for loop
                end
            end

            match = craft
        end)()
        -- We found something, stop iteration
        if match then
            return match.item
        end
    end
    return nil
end

---@param p Player
---@param skillID number
---@param ingredients Item[]
---@return number|nil itemID
Handlers.players.onCraft = function(p, skillID, ingredients)
    local jobID = SKILL_JOBS[skillID] or 0

    if not SKILLS[skillID] then
        JLogF("unknown skill {}", skillID)
        return nil
    end

    local crafts = SKILL_CRAFTS[skillID]
    if not crafts then
        error("hack attempt ?")
    end

    -- Convert GUID into template ID
    local guidForID = {}
    local stacks = {}

    for _, ingredient in ipairs(ingredients) do
        guidForID[ingredient:id()] = ingredient:guid()
        table.insert(stacks, ItemStack(ingredient:id(), ingredient:quantity()))
    end

    local outID = findCraft(crafts, stacks)
    if not outID then
        JLogF("unknown recipe")
        -- Unknown recipe
        return nil
    end

    -- Found something, craft it !

    for _, ingredient in ipairs(ingredients) do
        if not p:consumeItemGUID(ingredient:guid(), ingredient:quantity()) then
            error("hack attempt ?")
        end
    end

    -- TODO: Xp

    return outID
end