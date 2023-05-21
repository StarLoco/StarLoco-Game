local npc = Npc(227, 9041)
--TODO: Lié à la quête Alignement 30 Bonta
npc.colors = {6833220, 15921906, 16514044}
npc.accessories = {0, 941, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1237)
    end
end

RegisterNPCDef(npc)
