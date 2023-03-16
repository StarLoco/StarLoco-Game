local npc = Npc(295, 1207)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1530, {1164})
    elseif answer == 1164 then p:ask(1531, {1165})
    end
end

RegisterNPCDef(npc)