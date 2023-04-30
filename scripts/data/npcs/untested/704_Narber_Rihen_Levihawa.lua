local npc = Npc(704, 9048)

npc.sales = {
    {item=7359},
    {item=7360},
    {item=7361},
    {item=7362},
    {item=7363},
    {item=7364},
    {item=7365},
    {item=7366},
    {item=7367},
    {item=7371},
    {item=7372},
    {item=7330}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2926)
    end
end

RegisterNPCDef(npc)
