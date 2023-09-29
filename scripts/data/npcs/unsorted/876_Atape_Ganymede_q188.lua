local npc = Npc(876, 9059)
local questId = 188

npc.colors = {12571268, 6304805, 7119687}

npc.quests = {questId}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questId]

    if quest:availableTo(p) then
        if answer == 0 then
            quest:startFor(p, self.id)
            p:ask(3771, {3398})
        end
    end

    if quest:ongoingFor(p) then
        if answer == 0 then
            p:ask(3772, {3400})
        elseif answer == 3400 then
            p:endDialog()
        end
    end

    if quest:finishedBy(p) then
        p:ask(3773)
    end
end

RegisterNPCDef(npc)