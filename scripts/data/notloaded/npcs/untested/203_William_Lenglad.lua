local npc = Npc(203, 90)

npc.colors = {0, 5618448, 3828287}
npc.accessories = {0, 708, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4516)
    end
end

RegisterNPCDef(npc)
