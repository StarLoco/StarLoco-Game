local npc = Npc(37, 9024)

npc.sales = {
    {item=1407},
    {item=1408},
    {item=1409},
    {item=1410},
    {item=1411},
    {item=1412},
    {item=1413},
    {item=1414},
    {item=1415},
    {item=1416},
    {item=1417},
    {item=1418},
    {item=1418},
    {item=1419},
    {item=1420},
    {item=1421},
    {item=1422},
    {item=1423},
    {item=1424},
    {item=1425},
    {item=1434}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(81, {80, 7453, 7})
    elseif answer == 80 then p:ask(84)
    elseif answer == 7453 then p:ask(7465, {7259, 7260})
    elseif answer == 7259 then p:ask(7466, {7261})
    elseif answer == 7261 then p:ask(7468, {7263})
    elseif answer == 7263 then p:endDialog()
    elseif answer == 7260 then p:ask(7467, {7262})
    elseif answer == 7262 then p:ask(7469, {7264})
    elseif answer == 7264 then p:endDialog()
    end
end

RegisterNPCDef(npc)
