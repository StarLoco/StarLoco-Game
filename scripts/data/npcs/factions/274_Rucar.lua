local npc = Npc(274, 9030)
--TODO: Lié à la quête 68 Alignement Brâkmar
npc.colors = {393216, 13187642, 6820130}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1124)
    end
end

RegisterNPCDef(npc)
