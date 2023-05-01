local npc = Npc(730, 9025)

npc.colors = {3762885, 14818382, 3583536}

npc.sales = {
    {item=7931}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2656 then p:ask(3013, {2657, 2658})
    elseif answer == 2657 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2658 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2674 then p:ask(3036, {2679, 2677})
    elseif answer == 2677 then p:ask(3014, {2660})
    elseif answer == 2660 then p:ask(3015)
    elseif answer == 2679 then p:endDialog()
    elseif answer == 2644 then p:ask(3009, {2645, 2646})
    elseif answer == 2645 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2646 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2647 then p:ask(3010, {2648, 2649})
    elseif answer == 2648 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2649 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2650 then p:ask(3011, {2651, 2652})
    elseif answer == 2651 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2652 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2653 then p:ask(3012, {2654, 2655})
    elseif answer == 2654 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2655 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2639 then p:ask(3008, {2642, 2643})
    elseif answer == 2642 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    elseif answer == 2643 then p:ask(3004, {2639, 2644, 2647, 2650, 2656, 2653, 2674})
    end
end

RegisterNPCDef(npc)
