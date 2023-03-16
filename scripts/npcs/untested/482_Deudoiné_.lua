local npc = Npc(482, 9015)

npc.colors = {16777215, 12105913, 8729142}

npc.sales = {
    {item=493},
    {item=494},
    {item=495},
    {item=496},
    {item=922}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2163, {1991, 1992})
    elseif answer == 1991 then p:ask(2380, {4840})
    elseif answer == 1992 then p:ask(2381, {1995, 1993, 1997, 1996, 1994})
    end
end

RegisterNPCDef(npc)
