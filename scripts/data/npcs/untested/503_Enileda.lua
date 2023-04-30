local npc = Npc(503, 9069)

npc.gender = 1
npc.accessories = {0, 0, 779, 0, 0}

npc.sales = {
    {item=498},
    {item=499},
    {item=500}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2190, {1981, 1980})
    elseif answer == 1980 then p:ask(2375)
    elseif answer == 1981 then p:ask(2376, {1983, 1982, 1984})
    elseif answer == 1984 then p:ask(2379, {1990, 1989})
    elseif answer == 1989 then p:endDialog()
    elseif answer == 1982 then p:ask(2377, {1986, 1985})
    elseif answer == 1985 then p:endDialog()
    elseif answer == 1983 then p:ask(2378, {1988, 1987})
    elseif answer == 1987 then p:endDialog()
    end
end

RegisterNPCDef(npc)
