local npc = Npc(144, 9054)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        if p:getItem(963) then
            p:ask(469)
        else
            p:ask(469, {393})
        end
    elseif answer == 393 then
        p:addItem(963)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
