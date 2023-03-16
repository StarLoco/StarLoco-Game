local npc = Npc(1100, 100)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6327, {5574, 5575, 5573, 5572, 631})
    elseif answer == 5572 then p:ask(6328)
    elseif answer == 5573 then p:ask(6329)
    elseif answer == 5574 then p:ask(6330)
    elseif answer == 631 then p:endDialog()
    elseif answer == 5575 then p:ask(6330)
    end
end

RegisterNPCDef(npc)
