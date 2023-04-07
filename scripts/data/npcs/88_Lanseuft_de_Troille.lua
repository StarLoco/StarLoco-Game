local npc = Npc(88, 9003)

npc.colors = {0, 0, 0}

npc.barters = {
    {to={itemID=805, quantity= 1}, from= {
        {itemID=307, quantity= 200},
        {itemID=290, quantity= 90},
        {itemID=288, quantity= 75},
        {itemID=427, quantity= 60},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(243, {196, 195, 566})
    elseif answer == 195 then p:ask(3282, {293})
    elseif answer == 293 then p:ask(359)
    elseif answer == 196 then p:ask(245, {197})
    elseif answer == 566 then p:ask(663)
    end
end

RegisterNPCDef(npc)
