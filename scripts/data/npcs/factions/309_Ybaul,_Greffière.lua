local npc = Npc(309, 9013)
--TODO: Lié à la quête d'Alignement 20 Bonta
npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5695)
    end
end

RegisterNPCDef(npc)
