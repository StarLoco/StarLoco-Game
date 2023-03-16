local npc = Npc(791, 61)

npc.gender = 1
npc.accessories = {0, 6926, 6927, 0, 0}

npc.sales = {
    {item=6927},
    {item=6926},
    {item=6928},
    {item=6929}
}

RegisterNPCDef(npc)
