-- Returns a State Graph for a harvestable resource behavior using frame 1,2,3,5
local treeSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2},
    [AnimStates.IN_USE]= {frame=3, duration=1033, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=1033, next=AnimStates.READY}
}

RegisterAnimation(7500, AnimStates.READY, treeSG)
RegisterAnimation(7503, AnimStates.READY, treeSG)
RegisterAnimation(7505, AnimStates.READY, treeSG)
RegisterAnimation(7507, AnimStates.READY, treeSG)
RegisterAnimation(7509, AnimStates.READY, treeSG)
RegisterAnimation(7504, AnimStates.READY, treeSG)
RegisterAnimation(7508, AnimStates.READY, treeSG)
RegisterAnimation(7501, AnimStates.READY, treeSG)
RegisterAnimation(7502, AnimStates.READY, treeSG)
RegisterAnimation(7506, AnimStates.READY, treeSG)
RegisterAnimation(7541, AnimStates.READY, treeSG)
RegisterAnimation(7542, AnimStates.READY, treeSG)
RegisterAnimation(7557, AnimStates.READY, treeSG)
RegisterAnimation(7553, AnimStates.READY, treeSG)
RegisterAnimation(7554, AnimStates.READY, treeSG)
RegisterAnimation(7552, AnimStates.READY, treeSG)
