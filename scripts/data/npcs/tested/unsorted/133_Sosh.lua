local npc = Npc(133, 9034)

npc.sales = {
    {item=951}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(459)
    end
end

RegisterNPCDef(npc)
