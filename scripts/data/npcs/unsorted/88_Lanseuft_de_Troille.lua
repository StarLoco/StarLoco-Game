local npc = Npc(88, 9003)

npc.colors = {0, 0, 0}

npc.barters = {
    {to={itemID=805, quantity= 1}, from= {
        {itemID=307, quantity= 200},
        {itemID=290, quantity= 90},
        {itemID=288, quantity= 75},
        {itemID=427, quantity= 60}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasItem = p:getItem(382)
    if answer == 0 then p:ask(243, {566, 195, 196})
    elseif answer == 566 then p:ask(663)
    elseif answer == 195 then p:ask(244)
    elseif answer == 196 then p:ask(245, {197})
    elseif answer == 197 then
        if hasItem then
            p:ask(203, {167})
        else
            p:ask(246)
        end
    elseif answer == 167 and hasItem then
        if p:consumeItem(382, 1) then
            p:teleport(310, 361)
            p:endDialog()
        else
            p:ask(246)
        end
    end
end

RegisterNPCDef(npc)
