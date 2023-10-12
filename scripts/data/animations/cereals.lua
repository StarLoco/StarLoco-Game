local cerealSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2},
    [AnimStates.IN_USE]= {frame=3, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}



RegisterAnimation(7511, AnimStates.READY, cerealSG)
RegisterAnimation(7514, AnimStates.READY, cerealSG)
RegisterAnimation(7513, AnimStates.READY, cerealSG)
RegisterAnimation(7515, AnimStates.READY, cerealSG)
RegisterAnimation(7518, AnimStates.READY, cerealSG)
RegisterAnimation(7516, AnimStates.READY, cerealSG)
RegisterAnimation(7517, AnimStates.READY, cerealSG)
RegisterAnimation(7550, AnimStates.READY, cerealSG)
