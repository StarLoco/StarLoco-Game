-- Layer2 object animations (Find/fix frame indices using o[1-7].swf)
---@class AnimatedObjects
AnimatedObjects = {}

---@class AnimStates
---@field READY string Ready/ Open
---
---@field IN_USE string Being harvested / Closing
---@field NOT_READY string Not ready / Closed
---@field READYING string Regrowing / Opening
AnimStates = {
    READY = "ready",
    IN_USE = "inuse",
    NOT_READY = "notready",
    READYING = "readying"
}
