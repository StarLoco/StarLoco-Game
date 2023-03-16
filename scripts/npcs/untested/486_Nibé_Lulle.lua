local npc = Npc(486, 70)

npc.colors = {4872595, 9517626, 14062740}

npc.sales = {
    {item=1459},
    {item=1463},
    {item=1468},
    {item=1473}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2030, {1952})
    elseif answer == 1952 then p:ask(2347, {1953})
    elseif answer == 1953 then p:ask(2348, {2120})
    elseif answer == 2120 then p:endDialog()
    end
end

RegisterNPCDef(npc)
