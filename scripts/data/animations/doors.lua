-- Returns a State Graph for a door/entrance behavior using frame 1,2,3,4
local doorSG = function(inUseDuration, readyingDuration)
    return {
        [AnimStates.NOT_READY] = {frame=1},
        -- TODO: double check that we don't have a LOCKED state for doors
        [AnimStates.READYING] = {frame=2, duration=readyingDuration, next=AnimStates.READY},
        [AnimStates.READY] = {frame=3, overrides = {movement= 4}},
        [AnimStates.IN_USE]= {frame=4, duration=inUseDuration, next=AnimStates.NOT_READY}
    }
end

--- Minos Maze doors / levers
AnimatedObjects.MinosMazeBlueDoor   = RegisterAnimation(6570, AnimStates.NOT_READY, doorSG(600, 600))
AnimatedObjects.MinosMazeYellowDoor = RegisterAnimation(6574, AnimStates.NOT_READY, doorSG(600, 600))

--- Sliding rock cave entrance
AnimatedObjects.SlidingRock         = RegisterAnimation(6550, AnimStates.NOT_READY, doorSG(3700, 4700))
AnimatedObjects.SlidingRockIncarnam = RegisterAnimation(6571, AnimStates.NOT_READY, doorSG(3700, 3333))

--- Sliding mine cart
AnimatedObjects.SlidingMineCart     = RegisterAnimation(6553, AnimStates.NOT_READY, doorSG(8050, 666))
