local fishSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

AnimatedObjects.Fish_SmallSeaFish = NewAnimation(7530, AnimStates.READY, fishSG)
AnimatedObjects.Fish_AggressiveSalmoon = NewAnimation(7530, AnimStates.READY, fishSG)
AnimatedObjects.Fish_Octopwus = NewAnimation(7530, AnimStates.READY, fishSG)
AnimatedObjects.Fish_RiverFish = NewAnimation(7532, AnimStates.READY, fishSG)
AnimatedObjects.Fish_SmallSeaFish = NewAnimation(7529, AnimStates.READY, fishSG)
AnimatedObjects.Fish_BigRiverFish = NewAnimation(7537, AnimStates.READY, fishSG)
AnimatedObjects.Fish_SeaFish = NewAnimation(7531, AnimStates.READY, fishSG)
AnimatedObjects.Fish_BigSeaFish = NewAnimation(7538, AnimStates.READY, fishSG)
AnimatedObjects.Fish_GiantRiverFish = NewAnimation(7539, AnimStates.READY, fishSG)
AnimatedObjects.Fish_SludgyTrout = NewAnimation(7530, AnimStates.READY, fishSG)
AnimatedObjects.Fish_GiantSeaFish = NewAnimation(7540, AnimStates.READY, fishSG)
