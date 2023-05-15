local npc = Npc(215, 1197)
--TODO: Lié aux quêtes Alignement Bonta 20/28/45
npc.accessories = {0, 0, 945, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(970)
    end
end

RegisterNPCDef(npc)
