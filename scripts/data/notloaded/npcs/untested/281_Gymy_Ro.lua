local npc = Npc(281, 9043)

npc.gender = 1
npc.colors = {13089278, 2301008, 2369927}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1111)
    end
end

RegisterNPCDef(npc)
