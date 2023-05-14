local npc = Npc(146, 1056)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllCawottes = p:getItem(361, 100)

    if answer == 0 then
        if p:spellLevel(367) > 0 then
            p:ask(472)
        elseif hasAllCawottes then
            p:ask(472, {398})
        else
            p:ask(472)
        end
    elseif answer == 398 then
        if hasAllCawottes then
            if p:consumeItem(361, 100) then
                p:setSpellLevel(367, 1)
                p:ask(473)
            end
        end
    end
end

RegisterNPCDef(npc)
