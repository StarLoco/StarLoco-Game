local npc = Npc(375, 1222)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1591, {1219, 1215, 1216})
    end
end

RegisterNPCDef(npc)
