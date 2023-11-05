local mobCount = 1
local npcId = 843

local qs329 = QuestStep(329, 3557)
local q172 = Quest(172, {qs329})
local finalObjective = TalkWithQuestObjective(713, npcId)

qs329.objectives = function(p)
    if p:breed() == FecaBreed then          
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(704, 974, mobCount), finalObjective})(p)
    elseif p:breed() == EnutrofBreed then   
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(703, 972, mobCount), finalObjective})(p)
    elseif p:breed() == SramBreed then      
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(712, 972, mobCount), finalObjective})(p)
    elseif p:breed() == SadidaBreed then    
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(708, 972, mobCount), finalObjective})(p)
    elseif p:breed() == EcaflipBreed then   
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(706, 973, mobCount), finalObjective})(p)
    elseif p:breed() == IopBreed then       
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(705, 973, mobCount), finalObjective})(p)
    elseif p:breed() == CraBreed then       
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(701, 973, mobCount), finalObjective})(p)
    elseif p:breed() == XelorBreed then     
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(707, 974, mobCount), finalObjective})(p)
    elseif p:breed() == OsamodasBreed then  
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(710, 974, mobCount), finalObjective})(p)
    elseif p:breed() == EniripsaBreed then  
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(702, 984, mobCount), finalObjective})(p)
    elseif p:breed() == SacrierBreed then   
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(711, 984, mobCount), finalObjective})(p)
    elseif p:breed() == PandawaBreed then   
        return q172:SequentialObjectives( {KillMonsterSingleFightObjective(709, 984, mobCount), finalObjective})(p)
    end
    return{}
end

qs329.rewardFn = QuestBasicReward(105, 0)
