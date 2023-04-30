local npc = Npc(378, 9059)
--TODO: Tuto alternatif début du jeu (old tuto tainéla)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask()
    end
end

RegisterNPCDef(npc)
