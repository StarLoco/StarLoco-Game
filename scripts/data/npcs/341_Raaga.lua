local npc = Npc(341, 1205)
-- --TODO: Lié à la quête Alignement Brâkmar #26 > Premier NPC à parler

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1369)
    end
end

RegisterNPCDef(npc)
