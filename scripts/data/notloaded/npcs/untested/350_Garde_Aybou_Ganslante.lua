local npc = Npc(350, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask()
    end
end
--TODO MAP 6259 / FIND DIALOGS
RegisterNPCDef(npc)
