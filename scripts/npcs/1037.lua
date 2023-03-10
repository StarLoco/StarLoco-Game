local npc = Npc(1037, 101)

npc.colors = {14188326, 15975691, -1}
npc.accessories = {0, 6863, 6886, 8211, 7069}

function npc:onTalk(player, answer)
    if answer == 0 then player:ask(5482, {4590}) end
    if answer == 4590 then player:ask(5483, {4591}) end
    if answer == 4591 then player:ask(5484, {4592}) end
    if answer == 4592 then player:ask(5485, {4594,4593}) end
    if answer == 4593 then player:ask(5486, {4597}) end
    if answer == 4594 then player:ask(5487) end
    if answer == 4597 then player:endDialog() end
end

RegisterNPCDef(npc)