local npc = Npc(79, 9020)

npc.sales = {
    {item=697},
    {item=1325}
}

npc.barters = {
    {to={itemID=806, quantity= 1}, from= {
        {itemID=387, quantity= 100}
    }}
}

--TODO TEMPLE FECA
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(121, {215, 374})
    elseif answer == 215 then p:ask(268)
    elseif answer == 374 then p:ask(447)
    end
end

RegisterNPCDef(npc)
