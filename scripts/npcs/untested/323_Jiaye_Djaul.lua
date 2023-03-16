local npc = Npc(323, 1207)

npc.colors = {3740941, 2378267, 5669927}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1287)
    end
end

RegisterNPCDef(npc)
