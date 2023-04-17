local npc = Npc(1090, 121)

npc.gender = 1
npc.colors = {16297990, 10972695, 14861340}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6194)
    end
end

RegisterNPCDef(npc)
