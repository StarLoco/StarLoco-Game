-- Returns a State Graph for a harvestable resource behavior using frame 1,2,3,4
local treeSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=1033, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=4, duration=1033, next=AnimStates.READY}
}

AnimatedObjects.Tree_Ash= NewAnimation(7500, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Oak = NewAnimation(7503, AnimStates.READY, treeSG)


local cerealSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

AnimatedObjects.Cereal_Wheat = NewAnimation(7511, AnimStates.READY, cerealSG)