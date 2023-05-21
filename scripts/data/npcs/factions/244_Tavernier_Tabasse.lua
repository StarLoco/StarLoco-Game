local npc = Npc(244, 1207)

npc.sales = {
    {item=2085},
    {item=311}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1276)
    end
end

RegisterNPCDef(npc)
