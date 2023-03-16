local npc = Npc(594, 9008)

npc.sales = {
    {item=596},
    {item=1860}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2391, {2013, 2011, 2037})
    elseif answer == 2011 then p:ask(1169)
    elseif answer == 2013 then p:ask(2394, {2024})
    elseif answer == 2024 then p:ask(1170)
    end
end

RegisterNPCDef(npc)
