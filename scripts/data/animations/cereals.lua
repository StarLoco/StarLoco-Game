local cerealSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

AnimatedObjects.Cereal_Wheat = NewAnimation(7511, AnimStates.READY, cerealSG)
AnimatedObjects.Cereal_Hemp = NewAnimation(7514, AnimStates.READY, cerealSG)
AnimatedObjects.Cereal_Flax = NewAnimation(7513, AnimStates.READY, cerealSG)
AnimatedObjects.Cereal_Barley = NewAnimation(7515, AnimStates.READY, cerealSG)
AnimatedObjects.Cereal_Malt = NewAnimation(7518, AnimStates.READY, cerealSG)
AnimatedObjects.Cereal_Rye = NewAnimation(7516, AnimStates.READY, cerealSG)
AnimatedObjects.Cereal_Oats = NewAnimation(7517, AnimStates.READY, cerealSG)
AnimatedObjects.Cereal_Rice = NewAnimation(7550, AnimStates.READY, cerealSG)
