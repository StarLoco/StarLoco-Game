local npc = Npc(1283, 9222)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7678, {7703, 7705, 7706})
    elseif answer == 7703 then
        p:teleport(11976, 436)
        p:endDialog()
    elseif answer == 7705 then
        p:teleport(11976, 436)
        p:endDialog()
    elseif answer == 7706 then
        p:teleport(11976, 436)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
