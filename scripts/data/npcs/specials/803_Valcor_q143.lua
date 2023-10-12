local npc = Npc(803, 100)
--TODO: Lié à la quête 143
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3308, {2964, 2965})
    elseif answer == 2964 then p:endDialog()
    elseif answer == 2965 then p:endDialog()
    end
end

RegisterNPCDef(npc)
