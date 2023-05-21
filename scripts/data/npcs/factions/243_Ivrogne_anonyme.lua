local npc = Npc(243, 30)
--TODO: Lié à la quête Alignement 2 Brâkmar
npc.colors = {0, 13436165, 13633564}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1049, {750})
        elseif answer == 750 then
        p:ask(1049)
    end
end

RegisterNPCDef(npc)
