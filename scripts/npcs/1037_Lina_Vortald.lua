local npc = Npc(1037, 101)

npc.colors = {14188326, 15975691, -1}
npc.accessories = {0, 6863, 6886, 8211, 7069}

function npc:onTalk(o, answer)
    if answer == 0 then o:ask(5482, {4590})
    elseif answer == 4590 then o:ask(5483, {4591})
    elseif answer == 4591 then o:ask(5484, {4592})
    elseif answer == 4592 then o:ask(5485, {4594,4593})
    elseif answer == 4593 then o:ask(5486, {4597})
    elseif answer == 4594 then o:ask(5487)
    elseif answer == 4597 then o:endDialog() end
end

RegisterNPCDef(npc)
