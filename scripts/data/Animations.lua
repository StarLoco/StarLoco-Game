-- Layer2 object animations (Find/fix frame indices using o[1-7].swf)
---@class AnimatedObjects
AnimatedObjects = {}

---@class AnimStates
---@field READY string Ready/ Open
---@field LOCKED string Not available, player is using skill on it
---@field IN_USE string Falling down / Closing
---@field NOT_READY string Not ready / Closed
---@field READYING string Regrowing / Opening
AnimStates = {
    READY = "ready",
    LOCKED = "locked",
    IN_USE = "inuse",
    NOT_READY = "notready",
    READYING = "readying"
}
