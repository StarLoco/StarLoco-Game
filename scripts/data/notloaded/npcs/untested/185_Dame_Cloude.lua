local npc = Npc(185, 9012)

npc.gender = 1
npc.colors = {13922159, 15183606, 15368144}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5533)
    end
end

RegisterNPCDef(npc)
