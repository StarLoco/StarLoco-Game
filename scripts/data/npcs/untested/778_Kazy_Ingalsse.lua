local npc = Npc(778, 9032)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3176, {2798, 2799, 2800})
    end
end

RegisterNPCDef(npc)
