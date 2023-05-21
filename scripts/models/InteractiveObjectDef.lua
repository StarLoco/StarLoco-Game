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
---@return InteractiveObjectDef
function RegisterIODef(id, type, skills)
    local self = setmetatable({}, {
        __index = InteractiveObjectDef,
    })
    self.id = id
    self.type = type
    self.skills = skills

    if IO_DEFS[id] then
        error(string.format("InteractiveObject #%d already registered", self.id))
    end

    IO_DEFS[id] = self
    return self
end

---@param player Player
---@param skillId number
---@return boolean worked
function InteractiveObjectDef:onUseSkill(player, skillId)
    if not table.contains(self.skills, skillId) then
        JLogF("{} tried to use invalid skill #{} on IO #{}", player:name(), skillId, self.id)
        return false
    end

    -- TODO call skill handler

    return true
end