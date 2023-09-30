local npc = Npc(891, 9091)
local questID = 200
local questObjectiveId = 811

npc.customArtwork = 9100
npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]
    local finishQuest = function(xp)
        p:addXP(xp)
        quest:completeObjective(p, questObjectiveId)
        p:endDialog()
    end

    if quest:availableTo(p) and answer == 0 then
        quest:startFor(p, self.id)
        p:ask(3850, {3388, 3389})
        return
    end

    if quest:ongoingFor(p) then
        if answer == 0 then
            p:ask(3850, {3388, 3389})
        elseif answer == 3388 then
            finishQuest(6)
        elseif answer == 3389 then
            p:ask(3851, {3390, 3391})
        elseif answer == 3390 then
            finishQuest(12)
        elseif answer == 3391 then
            p:ask(3852, {3392, 3393})
        elseif answer == 3392 then
            finishQuest(18)
        elseif answer == 3393 then
            p:ask(3853, {3394})
        elseif answer == 3394 then
            finishQuest(24)
        end
        return
    end

    if quest:finishedBy(p) then
        p:ask(3855)
        return
    end
    p:endDialog()
end

RegisterNPCDef(npc)