local npc = Npc(761, 1357)

npc.colors = {16479068, 16479068, 16479068}

---@param p Player
function npc:onTalk(p, answer)
	p:ask(3145, {})
end
RegisterNPCDef(npc)
