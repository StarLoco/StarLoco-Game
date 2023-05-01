local npc = Npc(289, 9001)

npc.colors = {3807807, 16772076, 3807287}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1116)
    end
end

RegisterNPCDef(npc)
