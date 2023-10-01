-- Returns a State Graph for a door/entrance behavior using frame 1,2,3,4
local doorSG = function(inUseDuration, readyingDuration)
    return {
        [AnimStates.NOT_READY] = {frame=1},
        [AnimStates.READYING] = {frame=2, duration=readyingDuration, next=AnimStates.READY},
        [AnimStates.READY] = {frame=3, overrides = {movement= 4}},
        [AnimStates.IN_USE]= {frame=4, duration=inUseDuration, next=AnimStates.NOT_READY}
    }
end

--- Well
AnimatedObjects.Well = NewAnimation(7519, AnimStates.READY, {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE] = {frame=2, duration=433, next= AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=4},
    [AnimStates.READYING] = {frame=5, duration=400, next=AnimStates.READY}
})

--- Minos Maze doors / levers
AnimatedObjects.MinosMazeBlueDoor = NewAnimation(6570, AnimStates.NOT_READY, doorSG(600, 600))
AnimatedObjects.MinosMazeYellowDoor = NewAnimation(6574, AnimStates.NOT_READY, doorSG(600, 600))

AnimatedObjects.MinosMazeBlueSwitch = NewAnimation(7041, AnimStates.READY, {[AnimStates.READY] = {frame=1, interactive=true}, [AnimStates.IN_USE] = {frame=2, duration=600, next=AnimStates.READY}})
AnimatedObjects.MinosMazeYellowSwitch = NewAnimation(7044, AnimStates.READY, {[AnimStates.READY] = {frame=1, interactive=true}, [AnimStates.IN_USE] = {frame=2, duration=600, next=AnimStates.READY}})


--- Sliding rock cave entrance
AnimatedObjects.SlidingRock = NewAnimation(6550, AnimStates.NOT_READY, doorSG(3700, 4700))
--- Sliding mine cart
AnimatedObjects.SlidingMineCart = NewAnimation(6553, AnimStates.NOT_READY, doorSG(8050, 666))

