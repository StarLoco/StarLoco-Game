local npc = Npc(143, 9054)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        if p:getItem(962) then
            p:ask(468)
        else
            p:ask(468, {392})
        end
    elseif answer == 392 then
        p:addItem(962)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
