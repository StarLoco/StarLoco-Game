local npc = Npc(334, 1211)

npc.colors = {0, 0, 8553090}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1313)
    end
end

RegisterNPCDef(npc)
