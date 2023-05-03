local npc = Npc(679, 9006)

npc.sales = {
    {item=7401},
    {item=7402},
    {item=7412}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2831, {244})
    elseif answer == 244 then p:ask(307)
    end
end

RegisterNPCDef(npc)
