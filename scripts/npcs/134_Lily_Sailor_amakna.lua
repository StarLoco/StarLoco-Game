local npc = Npc(134, 9053)

npc.gender = 1

local price = 500
function npc:onTalk(player, answer)
    if answer == 0 then
        local responses = player:kamas() >= price and {387} or {}
        player:ask(460, responses)
    elseif answer == 387 then
        if player:modKamas(-price) then
            player:sendAction(-1, 2, "2")
            player:teleport(833, 141)
        end
        player:endDialog()
    end
end

RegisterNPCDef(npc)
