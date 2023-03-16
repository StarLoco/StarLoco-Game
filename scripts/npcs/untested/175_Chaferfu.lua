local npc = Npc(175, 1014)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(662, {598})
    elseif answer == 598 then p:ask(674, {599, 600})
    end
end

RegisterNPCDef(npc)