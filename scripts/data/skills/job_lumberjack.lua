--FIXME timing / Reward

local lumberjackRequirements = function(minLvl)
    return  {
        jobID = LumberjackJob,
        toolType = 19,
        jobLvl = minLvl
    }
end

local treeRespawnFn = respawnBetweenMillis(5000, 10000)

---@field maxCount number
local tempReward = function(itemID, maxCount)
    if maxCount <= 0 then error("wtf") end

    return function()
        return {ItemStack(itemID, math.random(1, maxCount))}
    end
end

registerGatherSkill(6, tempReward(303,5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(10, tempReward(460, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(33, tempReward(461, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(34, tempReward(449, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(35, tempReward(470, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(37, tempReward(471, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(38, tempReward(472, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(39, tempReward(473, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(40, tempReward(476, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(41, tempReward(474, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(139, tempReward(2358, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(141, tempReward(2357, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(154, tempReward(7013, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(155, tempReward(7016, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(158, tempReward(7014, 5), treeRespawnFn, lumberjackRequirements(1))
registerGatherSkill(178, tempReward(7925, 5), treeRespawnFn, lumberjackRequirements(1))
