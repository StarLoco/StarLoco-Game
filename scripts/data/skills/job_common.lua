-- SKILLS LINKED TO THE COMMON JOB that every player has

-- Draw water from well
registerGatherSkill(102, function(p)
    -- 311: Water
    return {ItemStack(311, math.random(1, 10))}
end, respawnBetweenMillis(180000, 720000))