local npc = Npc(258, 61)
--TODO: Lié à la quête Alignement 33 Brâkmar
npc.gender = 1
npc.colors = {10301633, 8005789, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1109)
    end
end

RegisterNPCDef(npc)
