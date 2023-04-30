local npc = Npc(792, 11)

npc.gender = 1
npc.accessories = {0, 2411, 2414, 0, 7087}

npc.sales = {
    {item=2411},
    {item=2416},
    {item=2422},
    {item=2425},
    {item=2428},
    {item=2419},
    {item=2414}
}

RegisterNPCDef(npc)
