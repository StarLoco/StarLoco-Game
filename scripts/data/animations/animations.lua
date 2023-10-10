--- Well
AnimatedObjects.Well = NewAnimation(7519, AnimStates.READY, {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE] = {frame=2, duration=433, next= AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=400, next=AnimStates.READY}
})

