local npc = Npc(294, 1207)
--TODO: Lié aux quêtes Alignement Brâkmar 41 & 42
npc.colors = {5643035, -1, -1}

npc.sales = {
    {item = 2088},
    {item = 311}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4420)
    end
end

RegisterNPCDef(npc)
