local npc = Npc(405, 9016)

npc.gender = 1
npc.colors = {14932679, 8388615, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1680)
    end
end

RegisterNPCDef(npc)
