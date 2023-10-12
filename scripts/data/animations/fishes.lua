local fishSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2},
    [AnimStates.IN_USE]= {frame=3, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

RegisterAnimation(7530, AnimStates.READY, fishSG)
RegisterAnimation(7530, AnimStates.READY, fishSG)
RegisterAnimation(7530, AnimStates.READY, fishSG)
RegisterAnimation(7532, AnimStates.READY, fishSG)
RegisterAnimation(7529, AnimStates.READY, fishSG)
RegisterAnimation(7537, AnimStates.READY, fishSG)
RegisterAnimation(7531, AnimStates.READY, fishSG)
RegisterAnimation(7538, AnimStates.READY, fishSG)
RegisterAnimation(7539, AnimStates.READY, fishSG)
RegisterAnimation(7530, AnimStates.READY, fishSG)
RegisterAnimation(7540, AnimStates.READY, fishSG)
