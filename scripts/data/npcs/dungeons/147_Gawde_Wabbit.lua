local npc = Npc(147, 1058)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(477, {402})
    elseif answer == 402 then
        local helm = p:gearAt(HeadSlot)
        local cloak = p:gearAt(BackSlot)
        local staff = p:gearAt(WeaponSlot)

        if helm ~= nil and helm:id() == 969 and cloak ~= nil and cloak:id() == 971 and staff ~= nil and staff:id() == 970 then
            p:ask(478, {404, 403})
        else
            p:ask(479)
        end
    elseif answer == 404 then
        p:endDialog()
    elseif answer == 403 then
        p:ask(333, {438})
    elseif answer == 438 then
        p:teleport(1783, 394)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
