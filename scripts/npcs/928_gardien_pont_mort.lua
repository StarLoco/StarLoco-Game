local npc = Npc(928, 30)

npc.colors = {15787994, 9594674, 16773874}
npc.accessories = {0, 0x228d, 0, 0, 0}

local bonusPods = 500
local coralWeight = 10
local fail = function(player)
    --TODO: Start Fight
    player:endDialog()
end

---@param player Player
function npc:OnTalk(player, answer)
    if answer == 0 then player:ask(4114, {3589, 3590})
    elseif answer == 3590 then player:endDialog()
    elseif answer == 3589 then player:ask(4115, {3594, 3592, 3593, 3591})
    elseif answer == 3594 or answer == 3592 or answer == 3593 or answer == 3591 then player:ask(4116, {3595, 3596, 3597})
        --elseif answer == 3597 then fail(player)
    elseif (answer == 3596 and player:gender() == 0) or (answer == 3595 and player:gender() == 1) then
        player:ask(4117, {3598, 3599, 3600})
        --elseif answer == 3600 then fail(player)
    elseif (answer == 3599 and not player:hasEmote(14)) or (answer == 3598 and player:hasEmote(14)) then
        player:ask(4118, {3601, 3602, 3603, 3604, 3605})
        --elseif answer == 3605 then fail(player)
    elseif answer >= 3601 and answer <= 3604 then
        local used, max = player:pods()
        local remainingBonus = max - used + bonusPods
        local count = remainingBonus / coralWeight
        local expectedIdx = math.min(count / 100 + 1, 4)
        local expected = expectedIdx + 3600
        if answer == expected then player:ask(4120, {3611, 3612, 3613, 3615, 3614}) else fail(player) end
        --elseif answer >= 3611 and answer <= 3613 or answer == 3615 then fail(player)
    elseif answer == 3614 then player:ask(4119, {3606, 3607, 3608, 3609, 3610})
        --elseif answer >= 3606 and answer <= 3609 then fail(player)
    elseif answer == 3610 then
        -- Finir Quete
        player:ask(4121, {3616})
    elseif answer == 3616 then
        -- Verif Statut Quête
        player:teleport(10692, 303)


    else fail(player)
    end
end

RegisterNPCDef(npc)