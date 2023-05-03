local npc = Npc(201, 10)

npc.colors = {4210752, 16635514, 14184973}
npc.accessories = {0, 698, 953, 1540, 0}

npc.sales = {
    {item=1460},
    {item=1468},
    {item=1459},
    {item=1463}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1241)
    end
end

RegisterNPCDef(npc)
