local npc = Npc(154, 9061)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(543, {499, 461, 460})
    elseif answer == 460 then p:ask(544, {462})
    elseif answer == 461 then p:ask(545)
    end
end

RegisterNPCDef(npc)
