local npc = Npc(177, 9059)

npc.colors = {6380875, 16777215, 11250604}

function npc:onTalk(player, answer)
    if answer == 0 then player:ask(677, {605})
    elseif answer == 605 then
        player:addItem(1728, 1, false)
        player:teleport(1856, 226)
        player:endDialog()
    end
end

RegisterNPCDef(npc)
