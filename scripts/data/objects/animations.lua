-- Layer2 object animations
AnimatedObjects = {}

-- Sliding rock cave entrance
AnimatedObjects.SlidingRock = NewAnimation(6550, "closed",{["closed"] = {1, 0}, ["open"] = {2, 282}, ["opened"] = {3, 0}, ["close"]= {4, 222}})
-- Sliding mine cart
AnimatedObjects.SlidingMineCart = NewAnimation(6553, "closed", {["closed"] = {1, 0}, ["open"] = {2, 24}, ["opened"] = {3, 0}, ["close"]= {4, 29}})

