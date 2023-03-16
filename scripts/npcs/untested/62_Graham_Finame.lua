local npc = Npc(62, 9015)

npc.sales = {
    {item=493},
    {item=494},
    {item=495},
    {item=496}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(38, {341, 508})
    elseif answer == 341 then p:ask(421, {343, 384, 342, 345, 344})
    elseif answer == 508 then p:ask(604)
    end
end

RegisterNPCDef(npc)
