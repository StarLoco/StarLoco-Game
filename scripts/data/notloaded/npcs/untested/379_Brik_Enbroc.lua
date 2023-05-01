local npc = Npc(379, 1155)

npc.sales = {
    {item=2542}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1621)
    end
end

RegisterNPCDef(npc)
