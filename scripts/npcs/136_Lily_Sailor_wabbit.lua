local npc = Npc(136, 9053)

npc.gender = 1

function npc:onTalk(player, answer)
    if answer == 0 then  player:ask(462, { 388 })
    elseif answer == 388 then
        player:teleport(167, 273)
        player:endDialog()
    end
end

RegisterNPCDef(npc)
