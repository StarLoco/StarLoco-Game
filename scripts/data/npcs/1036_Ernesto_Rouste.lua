local npc = Npc(1036, 120)

npc.colors = {1880009, 1880009, 12562815}
npc.accessories = {0, 0x1e00, 0, 0, 0}

---@param p Player
function npc:onTalk(p, answer)
    local dungeon = FungusDungeon
	if answer == 0 then
		local responses = {}
		if p:getItem(dungeon.keyID, 1) then table.insert(responses, dungeon.keyResponseID) end
		if dungeon:hasKeyChain(p) then table.insert(responses, dungeon.keychainResponseID) end
		table.insert(responses, 4586)
		p:ask(dungeon.questionID, responses)
	elseif answer == dungeon.keyResponseID then
		-- Use Key then teleport
		if p:consumeItem(dungeon.keyID, 1) then p:teleport(dungeon.tpDest[1], dungeon.tpDest[2]) end
		p:endDialog()
	elseif answer == dungeon.keychainResponseID then
		-- Use Keychain then teleport
		if dungeon:useKeyChain(p) then p:teleport(dungeon.tpDest[1], dungeon.tpDest[2]) end
		p:endDialog()
		-- Open dialog 4586
	elseif answer == 4586 then
		p:ask(5476, {})
	end
end

RegisterNPCDef(npc)