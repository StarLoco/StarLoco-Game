--FIXME timing / Reward

local lumberjackRequirements = {
    jobID = 2,
    toolType = 19,
}

---@field maxCount number
local tempReward = function(itemID, maxCount)
    if maxCount <= 0 then error("wtf") end

    return function()
        return {ItemStack(itemID, math.random(1, maxCount))}
    end
end

registerGatherSkill(6, tempReward(303,5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(10, tempReward(460, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(33, tempReward(461, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(34, tempReward(449, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(35, tempReward(470, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(37, tempReward(471, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(38, tempReward(472, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(39, tempReward(473, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(40, tempReward(476, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(41, tempReward(474, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(139, tempReward(2358, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(141, tempReward(2357, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(154, tempReward(7013, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(155, tempReward(7016, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(158, tempReward(7014, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
registerGatherSkill(178, tempReward(7925, 5), respawnBetweenMillis(5000, 10000), lumberjackRequirements)
