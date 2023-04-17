local npc = Npc(925, 30)
--TODO: Lié à la quête 232
npc.accessories = {0, 699, 0, 0, 0}

function npc:onTalk(p, answer)
    if p:mapID() == 10836 then
        if answer == 0 then
            p:ask(4135, {3630})
        elseif answer == 3630 then
            p:teleport(9402, 343)
            p:endDialog()
        end
    elseif p:mapID() == 9402 then
        if answer == 0 then
            p:ask(4159, {3646, 3784})
        elseif answer == 3646 then
            p:teleport(10836, 356)
            p:endDialog()
        elseif answer == 3784 then
            p:teleport(10417, 369)
            p:endDialog()
        end
    elseif p:mapID() == 10417 then
        p:ask(4128, {})
    elseif p:mapID() == 11101 then
        if answer == 0 then
            p:ask(4129, {3623})
        elseif answer == 3623 then
            p:teleport(10437, 250)
            p:endDialog()
        end
    elseif p:mapID() == 10437 then
        if answer == 0 then
            p:ask(4134, {3628})
        elseif answer == 3628 then
            p:teleport(10836, 356)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
