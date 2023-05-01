local npc = Npc(148, 1056)

---@param p Player
---@param answer number

--TODO: Trouver Dialogs sur Offi pcq c'est pas le gawdien de fin du dj cawotte (c'est le 149)
function npc:onTalk(p, answer)
    if answer == 0 then p:ask()
    end
end

RegisterNPCDef(npc)
