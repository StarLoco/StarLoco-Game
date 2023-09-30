local npc = Npc(382, 9059)
--TODO: Tuto début du jeu, premier npc utilisé = 378 donc je sais pas si/quand lui est utilisé

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask()
    end
end

RegisterNPCDef(npc)
