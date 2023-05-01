local npc = Npc(616, 31)

npc.gender = 1
npc.colors = {13074679, 16777215, 16316422}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2491)
    end
end

RegisterNPCDef(npc)
