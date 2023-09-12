-- InteractiveObjectDef
---@class InteractiveObjectDef
---@field id number
---@field type number
---@field skills number[]
InteractiveObjectDef = {}

---@type table<number, InteractiveObjectDef>
IO_DEFS = {}

---@param id number
---@param type number
---@param anim Animation
---@return InteractiveObjectDef
function RegisterIODef(id, type, skills, anim)
    local self = setmetatable({}, {
        __index = InteractiveObjectDef,
    })
    self.id = id
    self.type = type
    self.skills = skills
    self.anim = anim

    if IO_DEFS[id] then
        error(string.format("InteractiveObject #%d already registered", self.id))
    end

    IO_DEFS[id] = self
    return self
end

---@param player Player
---@param cellId number
---@param skillId number
---@return boolean worked
function InteractiveObjectDef:onUseSkill(player, cellId, skillId)
    if not table.contains(self.skills, skillId) then
        JLogF("{} tried to use invalid skill #{} on IO #{}", player:name(), skillId, self.id)
        return false
    end

    if not SKILLS[skillId] then return false end

    return SKILLS[skillId](player, cellId)
end