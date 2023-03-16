local npc = Npc(914, 9066)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4058, {3543, 3532, 3577, 353})
    elseif answer == 3543 then p:ask(4069, {534})
    elseif answer == 3577 then p:ask(4104, {3795, 369})
    elseif answer == 369 then p:ask(442, {370})
    elseif answer == 370 then p:ask(443, {371})
    elseif answer == 371 then p:ask(444)
    elseif answer == 3795 then p:ask(4308)
    elseif answer == 3532 then p:ask(4067, {356})
    elseif answer == 356 then p:endDialog()
    end
end

RegisterNPCDef(npc)
