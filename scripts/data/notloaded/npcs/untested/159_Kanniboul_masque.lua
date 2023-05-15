local npc = Npc(159, 9063)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    --TODO: Porté le masque kanniboule & etre level 40 mini
    if answer == 0 then p:ask(566, {493})
    end
end

RegisterNPCDef(npc)
