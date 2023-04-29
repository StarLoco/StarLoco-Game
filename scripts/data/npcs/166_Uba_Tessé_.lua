local npc = Npc(166, 9039)

--TODO: check la suite de "quête" que donne l'item
npc.sales = {
    {item=1468},
    {item=1463},
    {item=1460},
    {item=1459},
    {item=1473},
}
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(620, {528})
    elseif answer == 528 then p:ask(621, {529})
    elseif answer == 529 then
        if p:getItem(1469) then
            p:ask(623)
        else
            p:addItem(1469)
            p:ask(623)
        end
    end
end

RegisterNPCDef(npc)
