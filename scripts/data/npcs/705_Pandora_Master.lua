local npc = Npc(705, 1295)

---@param p Player
function npc:onTalk(p, answer)
    if answer == 0 then
		p:ask(2938, {2575})
    elseif answer == 2575 then
        p:addItem(7413, 1, false)
		p:addItem(7414, 1, false)
        p:teleport(8167, 266)
        p:endDialog()
    end
end
RegisterNPCDef(npc)