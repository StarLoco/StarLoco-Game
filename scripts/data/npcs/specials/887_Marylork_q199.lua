local npc = Npc(887, 9083)
--TODO: Lié à la quête 199
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3832, {3363, 336})
    elseif answer == 3363 then p:endDialog()
    end
end

RegisterNPCDef(npc)
