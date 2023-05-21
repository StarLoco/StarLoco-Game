local npc = Npc(756, 10)

npc.accessories = {0, 8009, 8007, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllItems = p:getItem(7904, 50) and p:getItem(7903, 50) and p:spellLevel(414) < 1
    if answer == 0 then
        local responses = hasAllItems and {2754, 2753} or {2753}
        p:ask(3127, responses)
    elseif answer == 2753 then p:ask(3128)
    elseif answer == 2754 and hasAllItems then p:ask(3129, {2755})
    elseif answer == 2755 and hasAllItems then p:ask(3130, {2756})
    elseif answer == 2756 and hasAllItems then p:ask(3131, {2757})
    elseif answer == 2757 and hasAllItems then
        p:consumeItem(7904, 50)
        p:consumeItem(7903, 50)
        p:setSpellLevel(414, 1)
        p:ask(3132)
    else
        p:endDialog()
    end
end

RegisterNPCDef(npc)
