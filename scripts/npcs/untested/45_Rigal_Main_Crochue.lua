local npc = Npc(45, 9023)

npc.sales = {
    {item=1264},
    {item=1265},
    {item=1266},
    {item=1267},
    {item=1268},
    {item=1269},
    {item=1270},
    {item=1271},
    {item=1272},
    {item=1273},
    {item=1274},
    {item=1275},
    {item=1276},
    {item=1277},
    {item=1278},
    {item=1279},
    {item=1280},
    {item=1281},
    {item=1282},
    {item=1283}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(98)
    end
end

RegisterNPCDef(npc)