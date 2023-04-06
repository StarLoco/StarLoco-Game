local npc = Npc(799, 1436)

---@param p Player
function npc:onTalk(p, answer)
    if p:mapID() == 6536 then 
        BontaRatDungeon:onTalkToGateKeeper(p, answer)
    elseif p:mapID() == 10213 then
        if answer == 0 then 
            p:ask(3292, {2962})
        elseif answer == 2962 then
            p:addItem(8476, 1, false)
            p:teleport(6536, 273)
            p:endDialog()
        end
    end
end
RegisterNPCDef(npc)