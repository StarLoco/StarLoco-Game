local npc = Npc(278, 9034)
--TODO: Lié à plusieurs quêtes d'Alignement
npc.sales = {
    {item = 579},
    {item = 951}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1102)
    end
end

RegisterNPCDef(npc)
