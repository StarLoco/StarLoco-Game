local npc = Npc(160, 1129)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        if p:spellLevel(366) < 1 then
            p:ask(588, {494})
        else
            p:ask(588)
        end
    elseif answer == 494 then
        p:setSpellLevel(366, 1)
        p:ask(589, {495})
    elseif answer == 495 then
        p:endDialog()
    end
end

RegisterNPCDef(npc)
