-- Returns a State Graph for a door/entrance behavior using frame 1,2,3,4
local doorSG = function(inUseDuration, readyingDuration)
    return {
        [AnimStates.NOT_READY] = {frame=1},
        [AnimStates.READYING] = {frame=2, duration=readyingDuration, next=AnimStates.READY},
        [AnimStates.READY] = {frame=3, overrides = {movement= 4}},
        [AnimStates.IN_USE]= {frame=4, duration=inUseDuration, next=AnimStates.NOT_READY}
    }
end

--- Minos Maze doors / levers
AnimatedObjects.MinosMazeBlueDoor = NewAnimation(6570, AnimStates.NOT_READY, doorSG(600, 600))
AnimatedObjects.MinosMazeYellowDoor = NewAnimation(6574, AnimStates.NOT_READY, doorSG(600, 600))

--- Sliding rock cave entrance
AnimatedObjects.SlidingRock = NewAnimation(6550, AnimStates.NOT_READY, doorSG(3700, 4700))
AnimatedObjects.SlidingRockIncarnam = NewAnimation(6571, AnimStates.NOT_READY, doorSG(3700, 3333))

--- Sliding mine cart
AnimatedObjects.SlidingMineCart = NewAnimation(6553, AnimStates.NOT_READY, doorSG(8050, 666))
