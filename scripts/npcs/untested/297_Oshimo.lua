local npc = Npc(297, 9005)

npc.accessories = {0, 0, 0, 7519, 0}

npc.sales = {
    {item=8319},
    {item=8316},
    {item=8318},
    {item=8317},
    {item=2092},
    {item=7529},
    {item=7530},
    {item=7528},
    {item=7531},
    {item=2091},
    {item=2093},
    {item=2239}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1150, {818, 819, 1239})
    elseif answer == 818 then p:ask(1151, {3576, 820})
    elseif answer == 820 then p:ask(1154, {822})
    elseif answer == 822 then p:ask(1156)
    elseif answer == 3576 then p:ask(4102)
    elseif answer == 819 then p:ask(1152)
    elseif answer == 1239 then p:ask(1606, {1240})
    end
end

RegisterNPCDef(npc)
