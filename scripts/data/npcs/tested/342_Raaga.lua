local npc = Npc(342, 1205)
--TODO: Lié à la quête Alignement Brâkmar #26 je ne sais pas si il est utilisé car l'ID 341 est déjà utilisé
--TODO: Mais comme il doit nous suivre jusqu'à une autre map c'est peut-être l'ID utilisé quand il se trouve sur la map finale ?

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask()
    end
end

RegisterNPCDef(npc)
