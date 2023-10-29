local npc = Npc(524, 101)
--TODO: Lié à la quête 43
npc.gender = 1
npc.colors = {11121888, 10490393, 15056833}

local questID = 43

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) or quest:ongoingFor(p) then
        if answer == 0 then p:ask(2245, {1878})
        elseif answer == 1878 then
            if quest:availableTo(p) then
                quest:startFor(p, self.id)
            end
            p:endDialog()
            p:forceFight({-1, {{469, {2}}, {469, {3}}, {469, {4}}}})
        end
        return
    end

    if quest:finishedBy(p) then
        p:ask(2295)
        return
    end
end

RegisterNPCDef(npc)
