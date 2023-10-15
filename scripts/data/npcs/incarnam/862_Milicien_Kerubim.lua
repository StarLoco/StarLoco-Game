local npc = Npc(862, 9019)

local questID = 179

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:ongoingFor(p) then
        if quest:canCompleteObjective(p, 749) then
            p:ask(3691)
            quest:completeObjective(p, 749)
            return
        end
    end

    if answer == 0 then p:ask(3693)
    end
end

RegisterNPCDef(npc)
