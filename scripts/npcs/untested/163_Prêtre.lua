local npc = Npc(163, 9000)

npc.sales = {
    {item=6660},
    {item=6662}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(613, {560, 559, 518, 2582})
    elseif answer == 560 then p:ask(658)
    elseif answer == 2582 then p:ask(2951, {2591, 2592})
    elseif answer == 559 then p:ask(657)
    end
end

RegisterNPCDef(npc)