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
    elseif answer == 2994 then p:ask(3341, {2993, 2990, 2980, 2998, 2983})
    elseif answer == 2995 then p:ask(3361, {3000})
    elseif answer == 3000 then p:ask(3341, {2993, 2990, 2980, 2998, 2983})
    elseif answer == 2980 then p:ask(3350, {2982, 2989, 3036, 3037, 3038})
    elseif answer == 2982 then p:endDialog()
    elseif answer == 3036 then p:endDialog()
    elseif answer == 3037 then p:endDialog()
    elseif answer == 2989 then p:endDialog()
    elseif answer == 3038 then p:endDialog()
    elseif answer == 2998 then p:ask(3362, {2999})
    elseif answer == 2999 then p:ask(3341, {2993, 2990, 2980, 2998, 2983})
    elseif answer == 2983 then p:ask(3352, {2984})
    elseif answer == 2984 then p:ask(3341, {2993, 2990, 2980, 2998, 2983})
    elseif answer == 2990 then p:ask(3359, {2991, 2992})
    elseif answer == 2992 then p:ask(3341, {2993, 2990, 2980, 2998, 2983})
    elseif answer == 2991 then p:endDialog()
    end
end

RegisterNPCDef(npc)
