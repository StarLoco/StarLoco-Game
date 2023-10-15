local npc = Npc(558, 9032)
--TODO: Lié à la quête 112, Quête Alignement Brâkmar permet de choisir l'ordre surineur une fois l'alignement > 40
npc.gender = 1

npc.sales = {
    {item = 577},
    {item = 468},  -- Vente temporaire de pain
    {item = 1737}, -- Vente temporaire de pain
    {item = 1738}  -- Vente temporaire de pain
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2342, {1948})
    elseif answer == 1948 then p:ask(334, {271})
    elseif answer == 271 then
        if p:tryLearnJob(FarmerJob) then
            p:ask(335)
        else
            p:ask(336)
        end
    end
end

RegisterNPCDef(npc)
