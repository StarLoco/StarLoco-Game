local npc = Npc(356, 9008)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1424, {1073, 1250, 1077})
    elseif answer == 1073 then p:ask(1430, {1074})
    elseif answer == 1074 then p:ask(1431, {1078})
    elseif answer == 1078 then p:ask(1436, {1079})
    elseif answer == 1079 then p:endDialog()
    elseif answer == 1250 then p:ask(1623, {1251})
    elseif answer == 1251 then p:ask(1624)
    elseif answer == 1077 then p:ask(1435)
    end
end

RegisterNPCDef(npc)
