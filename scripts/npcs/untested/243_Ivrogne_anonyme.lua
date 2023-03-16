local npc = Npc(243, 30)

npc.colors = {0, 13436165, 13633564}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1049, {75})
    end
end

RegisterNPCDef(npc)
