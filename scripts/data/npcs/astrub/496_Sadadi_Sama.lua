local npc = Npc(496, 100)

npc.gender = 0
npc.colors = {-1, 8739890, 328458}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2064, {1841})
    end
end

RegisterNPCDef(npc)
