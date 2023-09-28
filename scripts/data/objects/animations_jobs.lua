-- Returns a State Graph for a harvestable resource behavior using frame 1,2,3,4
local treeSG = {
    [AnimStates.READY] = {frame=1},
    [AnimStates.IN_USE]= {frame=2, duration=1033, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=4, duration=1033, next=AnimStates.READY}
}
AnimatedObjects.Tree_Oak = NewAnimation(7505, AnimStates.READY, treeSG)
