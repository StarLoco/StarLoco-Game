local npc = Npc(395, 9016)

npc.colors = {13476799, 8463428, 6761477}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1645)
    end
end

RegisterNPCDef(npc)
