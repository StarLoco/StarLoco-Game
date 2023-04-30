local npc = Npc(111, 9015)

npc.barters = {
    {to={itemID=795, quantity= 1}, from= {
        {itemID=407, quantity= 70},
        {itemID=379, quantity= 70}
    }},
    {to={itemID=797, quantity= 1}, from= {
        {itemID=379, quantity= 75},
        {itemID=407, quantity= 75},
        {itemID=432, quantity= 45},
        {itemID=448, quantity= 30}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(370, {302, 303})
    elseif answer == 302 then p:ask(371)
    elseif answer == 303 then p:ask(372, {304})
    elseif answer == 304 then p:ask(374)
    end
end

RegisterNPCDef(npc)
