local npc = Npc(314, 9070)

npc.gender = 1
npc.colors = {8698092, 16705276, 16777215}

npc.sales = {
    {item=1468},
    {item=1732},
    {item=1733}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1242)
    end
end

RegisterNPCDef(npc)
