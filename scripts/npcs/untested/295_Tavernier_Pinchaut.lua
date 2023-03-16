local npc = Npc(295, 1207)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1530, {1164})
    elseif answer == 1164 then p:ask(1531, {1165})
    elseif answer == 1165 then p:ask(1532, {1166, 1167})
    elseif answer == 1166 then p:ask(1533)
    elseif answer == 1167 then p:ask(1534)
    end
end

RegisterNPCDef(npc)
