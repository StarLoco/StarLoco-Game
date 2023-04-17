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
    elseif answer == 4840 then p:endDialog()
    elseif answer == 1992 then p:ask(2381, {1995, 1993, 1997, 1996, 1994})
    elseif answer == 1993 then p:ask(2382, {1999, 1998})
    elseif answer == 1998 then p:endDialog()
    elseif answer == 1994 then p:ask(2383, {2001, 2000})
    elseif answer == 2000 then p:endDialog()
    elseif answer == 1995 then p:ask(2384, {2003, 2002})
    elseif answer == 2002 then p:endDialog()
    elseif answer == 1996 then p:ask(2385, {2005, 2004})
    elseif answer == 2004 then p:endDialog()
    elseif answer == 1997 then p:ask(2386, {2007, 2006})
    elseif answer == 2006 then p:endDialog()
    end
end

RegisterNPCDef(npc)
