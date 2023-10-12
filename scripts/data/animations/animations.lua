--- Well
RegisterAnimation(7519, AnimStates.READY, {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2},
    [AnimStates.IN_USE] = {frame=3, duration=433, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=400, next=AnimStates.READY}
})

--- Crafting Workbenches
local workbenchesID = {
    7003,
    7011,
    7008,
    7009,
    7010,
    7013,
    7001,
    7002,
    7005,
    7006,
    7007,
    7012,
    7014,
    7015,
    7016,
    7018,
    7019,
    7020,
    7021,
    7022,
    7025,
    7024,
    7023,
    7028,
    7027,
    7036,
    7038,
    7037,
    7039,
    6631,
    6632,
}

local workbenchesSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2, interactive=true},
    [AnimStates.IN_USE] = {frame=3, interactive=true},
    [AnimStates.NOT_READY] = {frame=4, interactive=true}, -- Not used
    [AnimStates.READYING] = {frame=5, duration=1, interactive=true, next=AnimStates.READY}
}
for _, wbID in pairs(workbenchesID) do
    RegisterAnimation(wbID, AnimStates.READY, workbenchesSG)
end

-- Exist in cache but not linked to a sprites:
-- AnimatedObjects.Cauldron = RegisterAnimation([ObjID15], AnimStates.READY, workbenchesSG)
-- AnimatedObjects.Mill = RegisterAnimation([ObjID40], AnimStates.READY, workbenchesSG)
-- AnimatedObjects.Alembic = RegisterAnimation([ObjID62], AnimStates.READY, workbenchesSG)
-- AnimatedObjects.MortarAndPestle = RegisterAnimation([ObjID69], AnimStates.READY, workbenchesSG)
-- AnimatedObjects.Spinner = RegisterAnimation([ObjID83], AnimStates.READY, workbenchesSG)