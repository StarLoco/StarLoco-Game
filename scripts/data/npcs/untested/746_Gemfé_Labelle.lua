local npc = Npc(746, 110)

npc.colors = {9337448, 13621771, 15923691}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3112)
    end
end

RegisterNPCDef(npc)
