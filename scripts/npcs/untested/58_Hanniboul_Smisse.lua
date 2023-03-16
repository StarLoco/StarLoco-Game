local npc = Npc(58, 9015)

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
    elseif answer == 384 then p:ask(456, {38})
    elseif answer == 342 then p:ask(422, {347})
    elseif answer == 343 then p:ask(423, {346})
    elseif answer == 344 then p:ask(424, {348})
    elseif answer == 345 then p:ask(425, {349})
    elseif answer == 508 then p:ask(604)
    end
end

RegisterNPCDef(npc)
