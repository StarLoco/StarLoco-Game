local npc = Npc(855, 30)

local fedexQuestID = 179
local fedexLocalsQuestID = 180

npc.colors = {8388615, 16755731, 5117722}
npc.accessories = {7213, 702, 677, 7520, 0}
npc.customArtwork = 9098

npc.quests = {fedexQuestID, fedexLocalsQuestID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local fedexQuest = QUESTS[fedexQuestID]
    local fedexLocalsQuest = QUESTS[fedexLocalsQuestID]

    if fedexQuest:availableTo(p) then
        if answer == 0 then p:ask(3636, {3208, 3209})
        elseif answer == 3209 then p:ask(3640)
        elseif answer == 3208 then
            fedexQuest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if fedexLocalsQuest:availableTo(p) then
        if answer == 0 then p:ask(3637, {3240, 3239})
        elseif answer == 3239 then p:ask(3680) 
        elseif answer == 3240 then 
            p:ask(3681)
            fedexLocalsQuest:startFor(p, self.id)
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

    if fedexLocalsQuest:ongoingFor(p) then
        if fedexLocalsQuest:canCompleteObjective(p, 755) then
            p:ask(3638)
            fedexLocalsQuest:completeObjective(p, 755)
            return 
        end

        p:ask(3688)
        return
    end

    p:ask(3639)
end

RegisterNPCDef(npc)
