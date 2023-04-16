local npc = Npc(81, 9012)

npc.gender = 1
npc.colors = {1519221, 15642492, 14851125}

npc.sales = {
    {item=311},
    {item=351}
}

npc.barters = {
    {to={itemID=351, quantity= 1}, from= {
        {itemID=401, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(171, {145, 146})
    elseif answer == 145 then p:ask(172)
    elseif answer == 146 then p:ask(173)
    end
end

RegisterNPCDef(npc)
