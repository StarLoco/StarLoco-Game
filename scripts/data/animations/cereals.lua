local cerealSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

AnimatedObjects.Cereal_Wheat = NewAnimation(7511, AnimStates.READY, cerealSG)