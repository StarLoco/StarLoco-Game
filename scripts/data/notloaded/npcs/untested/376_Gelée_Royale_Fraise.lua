local npc = Npc(376, 1065)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1596, {1221, 1215, 1216})
    end
end

RegisterNPCDef(npc)
