local npc = Npc(597, 9034)

npc.sales = {
    {item=579}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2403, {2043, 2044})
    elseif answer == 2043 then p:ask(2404)
    elseif answer == 2044 then p:ask(2405, {2046, 2045})
    elseif answer == 2045 then p:endDialog()
    elseif answer == 2046 then p:ask(2406)
    end
end

RegisterNPCDef(npc)
