local npc = Npc(870, 9044)

npc.gender = 1

local questID = 185
---@param p Player
function npc:onTalk(p, answer)
    if p:questAvailable(questID) and answer == 0 then
        p:ask(3718, {3258,3257})
    elseif answer == 3258 then
        p:startQuest(questID)
        p:endDialog()
    elseif p:questOngoing(questID) then
        -- If we have the required items, complete objective
        if p:consumeItem(311, 4) and p:completeObjective(questID, 758) then
            p:endDialog()
            return
        end
        -- Ongoing quest dialog
        return p:ask(3720, {})
    elseif p:questFinished(questID) then
        p:ask(3719, {})
    -- elseif answer == 3257 then p:endDialog()
    else p:endDialog() end
end

RegisterNPCDef(npc)
