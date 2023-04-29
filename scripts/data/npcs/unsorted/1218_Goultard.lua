local npc = Npc(1218, 9212)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7661, {7661, 7665, 7660})
    elseif answer == 7661 then
        p:teleport(12227, 268)
        p:endDialog()
    elseif answer == 7665 then
        p:teleport(2184, 268)
        p:endDialog()
    elseif answer == 7660 then
        p:ask(7662, {7663})
    elseif answer == 7663 then
        p:endDialog()
    end
end

RegisterNPCDef(npc)
