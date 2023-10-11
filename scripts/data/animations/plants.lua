local plantSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.IN_USE]= {frame=2, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

AnimatedObjects.Plant_Edelweiss = NewAnimation(7536, AnimStates.READY, plantSG)
AnimatedObjects.Plant_WildMint = NewAnimation(7534, AnimStates.READY, plantSG)
AnimatedObjects.Plant_FiveLeafClover = NewAnimation(7533, AnimStates.READY, plantSG)
AnimatedObjects.Plant_FreyesqueOrchid = NewAnimation(7535, AnimStates.READY, plantSG)
AnimatedObjects.Cereal_Hemp = NewAnimation(7514, AnimStates.READY, plantSG)
AnimatedObjects.Cereal_Flax = NewAnimation(7513, AnimStates.READY, plantSG)
AnimatedObjects.Cereal_Pandkin = NewAnimation(7551, AnimStates.READY, plantSG)
