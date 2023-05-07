local npc = Npc(37, 9024)
--TODO: NPC Temple Xélor > Check dialogs offi en étant Xélor
npc.sales = {
    {item=786},
    {item=1428}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(81, {80, 78})
    elseif answer == 80 then p:ask(84)
    elseif answer == 78 then p:ask(82, {79})

    end
end

RegisterNPCDef(npc)
