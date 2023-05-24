---@type table<number, fun(p:Player, cellId:number)>
SKILLS = {}

local function registerCraftSkill(skillId)
    SKILLS[skillId] = function(p, cellId)
        return p:useCraftSkill(skillId, cellId)
    end
end

local function registerGatherSkill(skillId, itemId)
    SKILLS[skillId] = function(p, cellId)
        -- Let java side check for job/tool requirements
        -- TODO
    end
end

registerCraftSkill(1) -- Improve Dagger
registerGatherSkill(6, 303) -- Cut Ash
registerGatherSkill(10, 460) -- Cut Oak
registerCraftSkill(11) -- Create a ring
registerCraftSkill(12) -- Create an amulet
registerCraftSkill(13) -- Create boots
registerCraftSkill(14) -- Create a belt
registerCraftSkill(15) -- Carve a bow
registerCraftSkill(16) -- Carve a wand
registerCraftSkill(17) -- Carve a staff
registerCraftSkill(18) -- Craft a dagger
registerCraftSkill(19) -- Craft a hammer
registerCraftSkill(20) -- Craft a sword
registerCraftSkill(21) -- Craft a shovel
registerCraftSkill(22) -- Peel
-- TODO More


-- Save Zaap
SKILLS[44] = function(p, _)
    local md = p:map():def()
    p:savePosition(md.id, md.zaapCell)
    return true
end

-- Use Zaap
SKILLS[114] = function(p, _) p:openZaap() return true end

-- Use Switch
SKILLS[179] = function(p, cellId)
    local switchHandler = p:map():def().switches[cellId]
    if not switchHandler then return end
    switchHandler(md, p)
end
