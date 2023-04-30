local npc = Npc(870, 9044)

npc.gender = 1

local questID = 185

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) and answer == 0 then
        if answer == 0 then
            p:ask(3718, {3258, 3257})
        elseif answer == 3258 then
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if p:questOngoing(questID) then
        -- If we have the required items, complete objective
        if p:consumeItem(311, 4) then
            quest:completeObjective(p, 758)
            p:ask(3719)
            return
        end
        -- Ongoing quest dialog
        p:ask(3720)
        return
    end

    if p:questFinished(questID) then
        p:ask(3719)
        return
    end
    p:endDialog()
end

RegisterNPCDef(npc)
