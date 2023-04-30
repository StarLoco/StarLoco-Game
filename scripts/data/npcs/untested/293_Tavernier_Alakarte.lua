local npc = Npc(293, 1207)

npc.sales = {
    {item=2086},
    {item=311}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1149)
    end
end

RegisterNPCDef(npc)
