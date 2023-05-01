local npc = Npc(636, 1264)

npc.accessories = {0, 0, 0, 0, 7236}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2812, {2431, 2430, 2560})
    elseif answer == 2560 then p:endDialog()
    elseif answer == 2430 then p:ask(2813)
    elseif answer == 2431 then p:ask(2814)
    end
end

RegisterNPCDef(npc)
