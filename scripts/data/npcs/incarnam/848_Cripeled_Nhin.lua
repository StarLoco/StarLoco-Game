local npc = Npc(848, 30)

local questID = 180

npc.colors = {394758, 16121664, 13070517}
npc.accessories = {0, 7143, 0, 0, 0}
npc.customArtwork = 9096

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]
    if answer == 0 then p:ask(3588)
    end

    if quest:ongoingFor(p) then
        if quest:canCompleteObjective(p, 754) then
            p:ask(3683)
            quest:completeObjective(p, 754)
        end
    end
end

RegisterNPCDef(npc)
