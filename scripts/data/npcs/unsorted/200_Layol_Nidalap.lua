local npc = Npc(200, 80)

npc.colors = {16746054, 6707301, 14013909}
npc.accessories = {0, 0, 773, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1582)
    end
end

RegisterNPCDef(npc)
