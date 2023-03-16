local npc = Npc(463, 9030)

npc.sales = {
    {item=286},
    {item=492}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1903, {1648, 1647, 1947})
    elseif answer == 1648 then p:ask(1905, {1641})
    elseif answer == 1641 then p:ask(1906)
    elseif answer == 1947 then p:ask(132)
    elseif answer == 1647 then p:ask(1904, {1946})
    elseif answer == 1946 then p:ask(432, {358})
    end
end

RegisterNPCDef(npc)
