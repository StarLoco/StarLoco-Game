local npc = Npc(855, 30)

local fedexQuestID = 179

npc.colors = {8388615, 16755731, 5117722}
npc.accessories = {7213, 702, 677, 7520, 0}
npc.customArtwork = 9098

npc.quests = {fedexQuestID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local fedexQuest = QUESTS[fedexQuestID]

    if fedexQuest:availableTo(p) then
        if answer == 0 then p:ask(3636, {3208, 3209})
        elseif answer == 3209 then p:ask(3640)
        elseif answer == 3208 then
            fedexQuest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if fedexQuest:ongoingFor(p) then
        if fedexQuest:canCompleteObjective(p, 751) then
            p:ask(3635)
            fedexQuest:completeObjective(p, 751)
            return
        end
        p:ask(3688)
        return
    end

    if fedexQuest:finishedBy(p) then
        p:ask(3635)
        return
    end
end

RegisterNPCDef(npc)
