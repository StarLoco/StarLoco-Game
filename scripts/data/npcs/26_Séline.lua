local npc = Npc(26, 9013)

npc.gender = 1

npc.sales = {
    {item=1336},
    {item=497}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(101)
    end
end

RegisterNPCDef(npc)
