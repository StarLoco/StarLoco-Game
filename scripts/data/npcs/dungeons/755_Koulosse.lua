local npc = Npc(755, 1356)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3121, {2744})
    elseif answer == 2744 then p:ask(3122, {2745})
    elseif answer == 2745 then p:ask(3123, {2746})
    elseif answer == 2746 then p:ask(3124, {2747})
    elseif answer == 2747 then p:ask(3125, {2748})
    elseif answer == 2748 then p:ask(3126, {2749, 2750, 2751, 2752})
    elseif answer == 2749 then
        p:addItem(7887)
        p:teleport(8785, 295)
        p:endDialog()
    elseif answer == 2750 then
        p:addItem(7889)
        p:teleport(8785, 295)
        p:endDialog()
    elseif answer == 2751 then
        p:addItem(7890)
        p:teleport(8785, 295)
        p:endDialog()
    elseif answer == 2752 then
        p:addItem(7888)
        p:teleport(8785, 295)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
