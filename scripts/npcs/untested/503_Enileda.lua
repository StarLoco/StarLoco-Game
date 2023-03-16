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
    end
end

RegisterNPCDef(npc)
