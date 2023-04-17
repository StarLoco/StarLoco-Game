local npc = Npc(747, 30)
--TODO: Lié à la quête 131
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3044, {2688, 2689})
    elseif answer == 2688 then p:endDialog()
    elseif answer == 2689 then p:endDialog()
    end
end

RegisterNPCDef(npc)
