local npc = Npc(860, 9019)

local questID = 179

npc.accessories = {8101, 0, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:ongoingFor(p) then
        if quest:canCompleteObjective(p, 747) then
            p:ask(3674)
            quest:completeObjective(p, 747)
            return
        end
    end

    if answer == 0 then p:ask(3675)
    end
end

RegisterNPCDef(npc)
