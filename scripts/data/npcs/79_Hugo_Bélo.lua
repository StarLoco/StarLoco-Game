local npc = Npc(79, 9020)

npc.sales = {
    {item=697},
    {item=1325},
    {item=1184},
    {item=1185},
    {item=1186},
    {item=1187},
    {item=1188},
    {item=1189},
    {item=1190},
    {item=1191},
    {item=1192},
    {item=1193},
    {item=1194},
    {item=1195},
    {item=1196},
    {item=1197},
    {item=1198},
    {item=1199},
    {item=1200},
    {item=1201},
    {item=1202},
    {item=1203}
}

npc.barters = {
    {to={itemID=806, quantity= 1}, from= {
        {itemID=387, quantity= 100},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(121, {374, 215})
    elseif answer == 374 then p:ask(447)
    elseif answer == 215 then p:ask(268)
    end
end

RegisterNPCDef(npc)
