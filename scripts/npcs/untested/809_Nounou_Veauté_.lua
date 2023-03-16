local npc = Npc(809, 9012)

npc.gender = 1

npc.sales = {
    {item=9255},
    {item=9256},
    {item=9234},
    {item=9233}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3341, {2993, 2990, 2980, 2998, 2983})
    elseif answer == 2993 then p:ask(3360, {2994, 2995})
    elseif answer == 2980 then p:ask(3350, {2982, 2989, 3036, 3037, 3038})
    elseif answer == 2998 then p:ask(3362, {2999})
    elseif answer == 2983 then p:ask(3352, {2984})
    elseif answer == 2990 then p:ask(3359, {2991, 2992})
    end
end

RegisterNPCDef(npc)