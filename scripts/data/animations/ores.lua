local oreSG = {
    [AnimStates.READY] = {frame=1, interactive=true},
    [AnimStates.LOCKED] = {frame=2},
    [AnimStates.IN_USE]= {frame=3, duration=633, next=AnimStates.NOT_READY},
    [AnimStates.NOT_READY] = {frame=3},
    [AnimStates.READYING] = {frame=5, duration=1000, next=AnimStates.READY}
}

AnimatedObjects.Ore_Iron = NewAnimation(7520, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Silver = NewAnimation(7526, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Gold = NewAnimation(7527, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Bauxite = NewAnimation(7528, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Cobalt = NewAnimation(7525, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Tin = NewAnimation(7521, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Copper = NewAnimation(7522, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Manganese = NewAnimation(7524, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Bronze = NewAnimation(7523, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Dolomite = NewAnimation(7555, AnimStates.READY, oreSG)
AnimatedObjects.Ore_Silicate = NewAnimation(7556, AnimStates.READY, oreSG)
