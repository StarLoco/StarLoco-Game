local npc = Npc(1162, 40)

npc.colors = {7536640, 3163940, 12967391}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7069, {6632})
    end
end

RegisterNPCDef(npc)