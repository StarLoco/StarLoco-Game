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
---@param skills number[] defaults to empty
---@param anim Animation defaults to nil
---@param walkable boolean defaults to false
---@return InteractiveObjectDef
function RegisterIODef(id, type, skills, anim, walkable)
    local self = setmetatable({}, {
        __index = InteractiveObjectDef,
    })
    self.id = id
    self.type = type
    self.skills = skills or {}
    self.anim = anim
    self.walkable = walkable or false

    if IO_DEFS[id] then
        error(string.format("InteractiveObject #%d already registered", self.id))
    end

    IO_DEFS[id] = self

    RegisterObjectDef(self.id, self.skills, self.walkable)
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