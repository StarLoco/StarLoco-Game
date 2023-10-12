local plantSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2},
    [AnimStates.IN_USE]= {frame=3, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

RegisterAnimation(7536, AnimStates.READY, plantSG)
RegisterAnimation(7534, AnimStates.READY, plantSG)
RegisterAnimation(7533, AnimStates.READY, plantSG)
RegisterAnimation(7535, AnimStates.READY, plantSG)
RegisterAnimation(7514, AnimStates.READY, plantSG)
RegisterAnimation(7513, AnimStates.READY, plantSG)
RegisterAnimation(7551, AnimStates.READY, plantSG)
