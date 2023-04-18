local npc = Npc(66, 9036)

npc.sales = {
    {item=497}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(53)
    end
end

RegisterNPCDef(npc)
