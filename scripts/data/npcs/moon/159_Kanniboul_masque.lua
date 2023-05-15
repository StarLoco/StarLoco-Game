local npc = Npc(159, 9063)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    --TODO: Porté le masque kanniboule & etre level 40 mini
    if answer == 0 then p:ask(566, {493})
    elseif answer == 493 then
        local helm = p:gearAt(HeadSlot)
        if helm ~= nil and helm:id() == 1088 and p:level() >= 40 then
            p:ask(586, {497})
        else
            p:ask(587)
        end
    elseif answer == 497 then
        p:teleport(1765, 211)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
