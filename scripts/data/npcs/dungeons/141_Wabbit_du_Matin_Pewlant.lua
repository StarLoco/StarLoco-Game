local npc = Npc(141, 9054)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        if p:getItem(961) then
            p:ask(467)
        else
            p:ask(467, {391})
        end
    elseif answer == 391 then
        p:addItem(961)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
