local npc = Npc(859, 9079)

local questID = 182
local fedexLocalsQuestID = 180

npc.gender = 1
npc.accessories = {0, 2474, 6916, 0, 0}

npc.quests = {fedexLocalsQuestID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]
    local fedexLocalsQuest = QUESTS[fedexLocalsQuestID]

    if quest:ongoingFor(p) then 
        if p:consumeItem(8529, 1) then
            quest:completeObjective(p,746)
            p:ask(3666, {3233, 3232})
            return
        end
        error("hack attempt")
        return
    end

    if fedexLocalsQuest:ongoingFor(p) then
        if fedexLocalsQuest:canCompleteObjective(p, 753) then
            p:ask(3682)
            fedexLocalsQuest:completeObjective(p, 753)
        end
        return
    end

    if quest:finishedBy(p) then
        if answer == 0 then p:ask(3665)
        elseif answer == 3232 then p:endDialog()
        elseif answer == 3233 then p:ask(3667, {3234})
        elseif answer == 3234 then p:endDialog()
        end
        return
    end

    if answer == 0 then p:ask(3665)
    end

end

RegisterNPCDef(npc)
