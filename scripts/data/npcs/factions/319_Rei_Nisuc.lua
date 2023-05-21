local npc = Npc(319, 1207)
--TODO: Lié à plusieurs quêtes d'Alignement Bonta
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1272)
    end
end

RegisterNPCDef(npc)
