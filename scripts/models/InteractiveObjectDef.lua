-- InteractiveObjectDef
---@class InteractiveObjectDef
---@field id number
---@field type number
---@field onUseHandlers table<number, fun(p:Player)> key: skillId, value: handler
InteractiveObjectDef = {}

---@type table<number, InteractiveObjectDef>
IO_DEFS = {}

---@param id number
---@param type number
---@return InteractiveObjectDef
function RegisterIODef(id, type)
    local self = setmetatable({}, {
        __index = InteractiveObjectDef,
    })
    self.id = id
    self.type = type
    self.onUseHandlers = {}

    if IO_DEFS[id] then
        error(string.format("InteractiveObject #%d already registered", self.id))
    end

    IO_DEFS[id] = self
    return self
end