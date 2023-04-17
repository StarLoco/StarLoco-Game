local npc = Npc(389, 1228)

npc.gender = 1
npc.colors = {12076438, 10705571, 11750041}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1627, {125})
    end
end

RegisterNPCDef(npc)
