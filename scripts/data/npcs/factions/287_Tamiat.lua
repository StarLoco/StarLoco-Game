local npc = Npc(287, 9069)
--TODO: Lié à la quête d'Alignement 18 Brâkmar
npc.gender = 1
npc.colors = {16032712, 6492003, 0}
npc.accessories = {0, 698, 947, 1728, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1128)
    end
end

RegisterNPCDef(npc)
