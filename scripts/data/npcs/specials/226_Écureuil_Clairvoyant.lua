local npc = Npc(226, 1046)
--TODO: NPC lié à l'émote Pierre/Feuille/Ciseaux
npc.accessories = {0, 1907, 0, 0, 0}

---@param p Player
---@param answer number

function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1380)
    end
end

RegisterNPCDef(npc)
