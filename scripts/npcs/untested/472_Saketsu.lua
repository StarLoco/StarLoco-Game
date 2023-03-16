local npc = Npc(472, 110)

npc.colors = {16705257, 3939862, 16629956}
npc.accessories = {0, 0, 2446, 0, 0}

npc.sales = {
    {item=1945}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2115, {1973, 1832})
    elseif answer == 1973 then p:ask(1181, {851, 6140})
    elseif answer == 851 then p:ask(1182)
    elseif answer == 6140 then p:endDialog()
    elseif answer == 1832 then p:endDialog()
    end
end

RegisterNPCDef(npc)
