local npc = Npc(484, 9035)

npc.sales = {
    {item=454}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2210, {2015, 2014})
    elseif answer == 2014 then p:ask(2395)
    elseif answer == 2015 then p:ask(2396, {2017, 2016})
    elseif answer == 2016 then p:endDialog()
    end
end

RegisterNPCDef(npc)
