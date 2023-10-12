
-- Minos maze levers
RegisterAnimation(7041, AnimStates.READY, {[AnimStates.READY] = {frame=1, interactive=true}, [AnimStates.IN_USE] = {frame=2, duration=600, next=AnimStates.READY}})
RegisterAnimation(7044, AnimStates.READY, {[AnimStates.READY] = {frame=1, interactive=true}, [AnimStates.IN_USE] = {frame=2, duration=600, next=AnimStates.READY}})

