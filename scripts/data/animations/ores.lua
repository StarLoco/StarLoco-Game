local oreSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2},
    [AnimStates.IN_USE]= {frame=3, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

RegisterAnimation(7520, AnimStates.READY, oreSG)
RegisterAnimation(7526, AnimStates.READY, oreSG)
RegisterAnimation(7527, AnimStates.READY, oreSG)
RegisterAnimation(7528, AnimStates.READY, oreSG)
RegisterAnimation(7525, AnimStates.READY, oreSG)
RegisterAnimation(7521, AnimStates.READY, oreSG)
RegisterAnimation(7522, AnimStates.READY, oreSG)
RegisterAnimation(7524, AnimStates.READY, oreSG)
RegisterAnimation(7523, AnimStates.READY, oreSG)
RegisterAnimation(7555, AnimStates.READY, oreSG)
RegisterAnimation(7556, AnimStates.READY, oreSG)
