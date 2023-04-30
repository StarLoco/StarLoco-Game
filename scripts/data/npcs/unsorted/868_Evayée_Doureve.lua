local npc = Npc(868, 101)
local questId = 183
local questObjectiveId = 756

npc.gender = 1
npc.colors = {16310700, 15239964, 11627841}
npc.accessories = {0, 703, 0, 0, 0}
npc.customArtwork = 9099
npc.quests = {questId}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questId]
    local finishQuest = function(xp)
        p:addXP(xp)
        quest:completeObjective(p, questObjectiveId)
        p:endDialog()
    end

    if quest:availableTo(p) then
        if answer == 0 then
            p:ask(3706, {3248})
        elseif answer == 3248 and quest:startFor(p, self.id) then
            p:ask(3707, {3250, 3249})
        end
        return
    end

    if quest:ongoingFor(p) then
        if answer == 0 then
            p:ask(3707, {3250, 3249})
        elseif answer == 3249 then
            p:ask(3708, {3252, 3251})
        elseif answer == 3250 then
            finishQuest(5)
        elseif answer == 3251 then
            p:ask(3709, {3253})
        elseif answer == 3252 or answer == 3253 then
            finishQuest(10)
        end
        return
    end

    if quest:finishedBy(p) then
        p:ask(3713)
        return
    end
end

RegisterNPCDef(npc)