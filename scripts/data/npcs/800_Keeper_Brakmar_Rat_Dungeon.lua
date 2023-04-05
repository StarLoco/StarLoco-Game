local npc = Npc(800, 1535)

---@param p Player
function npc:onTalk(p, answer)
    if p:mapID() == 6738 then 
        BrakmarRatDungeon:onTalkToGateKeeper(p, answer)
    elseif p:mapID() == 10213 then
        if answer == 0 then 
            p:ask(3294, {2963})
        elseif answer == 2963 then
            p:addItem(8477, 1, false)
            p:teleport(6738, 213)
            p:endDialog()
        end
    end
end
RegisterNPCDef(npc)