local npc = Npc(755, 1356)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3121, {2744})
    elseif answer == 2744 then p:ask(3122, {2745})
    elseif answer == 2745 then p:ask(3123, {2746})
    elseif answer == 2746 then p:ask(3124, {2747})
    elseif answer == 2747 then p:ask(3125, {2748})
    elseif answer == 2748 then p:ask(3126, {2749, 2750, 2751, 2752})
    end
end

RegisterNPCDef(npc)
