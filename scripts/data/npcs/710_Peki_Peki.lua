local npc = Npc(710, 1297)

---@param p Player
function npc:onTalk(p, answer)
    if answer == 0 then
		p:ask(2944, {2580})
    elseif answer == 2575 then
        p:addItem(6978, 1, false)
        p:teleport(8467, 241)
        p:endDialog()
    end
end
RegisterNPCDef(npc)
