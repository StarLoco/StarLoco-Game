local npc = Npc(149, 1056)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        p:ask(500, {415})
    elseif answer == 415 then
        p:teleport(167, 273)
        p:endDialog()
    end
end
--TODO : SECOND DIALOG APRES QUON LUI A PARLER > 500, {415} > 415 teleport to 167, 273
RegisterNPCDef(npc)
