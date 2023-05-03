local npc = Npc(928, 30)
local questID = 230

npc.colors = {15787994, 9594674, 16773874}
npc.accessories = {0, 0x228d, 0, 0, 0}

local bonusPods = 500
local coralWeight = 10

---@param p Player
local fail = function(p)
    p:endDialog()
    p:forceFight({-1, {
        {1094, {5}
    }}})
end

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]
    if answer == 0 then
        if quest:finishedBy(p) then  p:ask(4122, {3616})
        elseif quest:ongoingFor(p) then p:ask(4115, {3594, 3592, 3593, 3591})
        else p:ask(4114, {3589, 3590}) end
    elseif answer == 3590 then
        p:endDialog()
    elseif answer == 3589 then
        quest:startFor(p, self.id)
        p:ask(4115, {3594, 3592, 3593, 3591})
    elseif answer == 3594 or answer == 3592 or answer == 3593 or answer == 3591 then
        p:ask(4116, {3595, 3596, 3597})
        --elseif answer == 3597 then fail(p)
    elseif (answer == 3596 and p:gender() == 0) or (answer == 3595 and p:gender() == 1) then
        p:ask(4117, {3598, 3599, 3600})
        --elseif answer == 3600 then fail(p)
    elseif (answer == 3599 and not p:hasEmote(14)) or (answer == 3598 and p:hasEmote(14)) then
        p:ask(4118, {3601, 3602, 3603, 3604, 3605})
        --elseif answer == 3605 then fail(p)
    elseif answer >= 3601 and answer <= 3604 then
        local used, max = p:pods()
        local remainingBonus = max - used + bonusPods
        local count = remainingBonus / coralWeight
        if count<50 then fail(p) end -- Should never happen, unless player has too many items somehow
        local expectedIdx = math.min(count / 100 + 1, 4)
        local expected = expectedIdx + 3600
        if answer == expected then p:ask(4120, {3611, 3612, 3613, 3615, 3614}) else fail(p) end
        --elseif answer >= 3611 and answer <= 3613 or answer == 3615 then fail(p)
    elseif answer == 3614 then p:ask(4119, {3606, 3607, 3608, 3609, 3610})
        --elseif answer >= 3606 and answer <= 3609 then fail(p)
    elseif answer == 3610 then
        quest:completeObjective(p, 940)
        self:onTalk(p, 0) -- This makes sure we have completed the quest
    elseif quest:finishedBy(p) and answer == 3616 then p:teleport(10692, 303)
    else fail(p)
    end
end

RegisterNPCDef(npc)
