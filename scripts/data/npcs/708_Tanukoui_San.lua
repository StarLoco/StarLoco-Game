local npc = Npc(708, 1283)

---@param p Player
function npc:onTalk(p, answer)
    if answer == 0 then
		p:ask(2942, {2578})
    elseif answer == 2578 then
        p:addItem(7415, 1, false)
        p:teleport(8236, 370)
        p:endDialog()
    end
end
RegisterNPCDef(npc)