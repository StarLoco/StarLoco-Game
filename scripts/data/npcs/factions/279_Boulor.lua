local npc = Npc(279, 9045)
--TODO: Lié à la quête d'Alignement 31 Brâkmar

npc.colors = {2069314, 5603222, 5439488}
npc.accessories = {40, 0, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1104)
    end
end

RegisterNPCDef(npc)
