local npc = Npc(753, 9005)

--TODO: Sniff couleurs/accessories offi
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        p:ask(3118, {2741, 2787})
    elseif answer == 2741 then
        p:teleport(9052, 268)
        p:endDialog()
    elseif answer == 2787 then
        p:endDialog()
    end
end

RegisterNPCDef(npc)
