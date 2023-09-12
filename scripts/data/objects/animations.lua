-- Layer2 object animations (Find/fix frame indices using o[1-7].swf)
AnimatedObjects = {}

-- Returns a State Graph for the usual READY/IN USE/NOT_READY/READYING behavior using frame 1,2,3,4
local basicSG = function(inUseDuration, readyingDuration)
    return {
        [AnimStates.NOT_READY] = {1},
        [AnimStates.READYING] = {2, readyingDuration, AnimStates.READY},
        [AnimStates.READY] = {3},
        [AnimStates.IN_USE]= {4, inUseDuration, AnimStates.NOT_READY}
    }
end

-- Well
AnimatedObjects.Well = NewAnimation(7519, AnimStates.READY, {[AnimStates.READY] = {1, 0}, [AnimStates.IN_USE] = {3, 26, AnimStates.NOT_READY}, [AnimStates.NOT_READY] = {3, 0}, [AnimStates.READYING] = {5, 24, AnimStates.READY} } )

-- Sliding rock cave entrance
AnimatedObjects.SlidingRock = NewAnimation(6550, AnimStates.NOT_READY, basicSG(222, 282))
-- Sliding mine cart
AnimatedObjects.SlidingMineCart = NewAnimation(6553, AnimStates.NOT_READY, basicSG(29, 24))

