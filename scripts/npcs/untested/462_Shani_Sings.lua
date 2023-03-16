local npc = Npc(462, 9033)

npc.gender = 1

npc.sales = {
    {item=491}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1918, {2008, 1655, 1657})
    elseif answer == 1655 then p:ask(1919)
    elseif answer == 2008 then p:ask(2387, {2010, 2009})
    elseif answer == 1657 then p:ask(1921, {1658})
    end
end

RegisterNPCDef(npc)
