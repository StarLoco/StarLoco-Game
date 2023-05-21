local npc = Npc(336, 1197)
--TODO: Lié aux quêtes d'Alignement 23 & 70 Bonta
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1334)
    end
end

RegisterNPCDef(npc)
