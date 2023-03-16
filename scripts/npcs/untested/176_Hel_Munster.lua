local npc = Npc(176, 9067)

npc.sales = {
    {item=1567},
    {item=1572},
    {item=1634},
    {item=7494}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(664, {573, 572, 571, 574})
    elseif answer == 571 then p:ask(665)
    elseif answer == 572 then p:ask(666, {2909, 2910, 2911, 2912, 2914, 2915, 2916, 2918, 2919, 2920, 2921, 2922})
    elseif answer == 2922 then p:endDialog()
    elseif answer == 573 then p:ask(667, {575, 576})
    elseif answer == 576 then p:ask(670)
    elseif answer == 575 then p:ask(669)
    elseif answer == 574 then p:ask(668)
    end
end

RegisterNPCDef(npc)
