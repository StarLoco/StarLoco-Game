local npc = Npc(312, 9012)
--TODO: Lié à la quête d'Alignement 2 Brâkmar
npc.gender = 1
npc.colors = {14408668, 8468283, 6039595}

npc.sales = {
    {item = 311},
    {item = 2044}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1230, {927})
    elseif answer == 927 then
        p:ask(1231)
    end
end

RegisterNPCDef(npc)
