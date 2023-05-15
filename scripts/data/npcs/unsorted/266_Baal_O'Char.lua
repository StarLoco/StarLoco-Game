local npc = Npc(266, 20)
--TODO: Lié à la quête Alignement 70 Brâkmar
npc.colors = {15371034, 3737856, 16134696}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1103)
    end
end

RegisterNPCDef(npc)
