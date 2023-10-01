-- Returns a State Graph for a harvestable resource behavior using frame 1,2,3,5
local treeSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=1033, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1033, next=AnimStates.READY}
}
AnimatedObjects.Tree_Ash = NewAnimation(7500, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Oak = NewAnimation(7503, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Yew = NewAnimation(7505, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Ebony = NewAnimation(7507, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Elm = NewAnimation(7509, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Maple = NewAnimation(7504, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Charm = NewAnimation(7508, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Chestnut = NewAnimation(7501, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Walnut = NewAnimation(7502, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Cherry = NewAnimation(7506, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Bombu = NewAnimation(7541, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Oliviolet = NewAnimation(7542, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Kaliptus = NewAnimation(7557, AnimStates.READY, treeSG)
AnimatedObjects.Tree_Bamboo = NewAnimation(7553, AnimStates.READY, treeSG)
AnimatedObjects.Tree_DarkBamboo = NewAnimation(7554, AnimStates.READY, treeSG)
AnimatedObjects.Tree_HolyBamboo = NewAnimation(7552, AnimStates.READY, treeSG)


local cerealSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

AnimatedObjects.Cereal_Wheat = NewAnimation(7511, AnimStates.READY, cerealSG)