local npc = Npc(870, 9044)

local questID = 185
local talkQuestID = 186

npc.gender = 1

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]
    local talkQuest = QUESTS[talkQuestID]

    if quest:availableTo(p) then
        if answer == 0 then
            p:ask(3718, {3258, 3257})
        elseif answer == 3258 then
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if quest:ongoingFor(p) then
        if quest:tryCompleteBringItemObjectives(p, self.id) then
            p:ask(3719)
        end
        
        p:ask(3720)
        return
    end

    if talkQuest:ongoingFor(p) then
        if talkQuest:canCompleteObjective(p, 759) then
            p:ask(3730, {3276, 3275})
            talkQuest:completeObjective(p, 759)
        elseif answer == 3276 or answer == 3275 then p:endDialog()
        end
        return
    end

    if quest:finishedBy(p) and talkQuest:finishedBy(p) then
        p:ask(3721)
        else
        p:ask(3719)         
        return
    end

end

RegisterNPCDef(npc)
