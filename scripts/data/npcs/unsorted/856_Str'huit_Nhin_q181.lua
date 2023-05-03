local npc = Npc(856, 30)
local questID = 181

npc.colors = {16711680, 2949120, 1245184}
npc.accessories = {0, 0x2bd, 0, 0, 0}
npc.customArtwork = 9097

npc.quests = {questID}


---@param p Player
---@param answer number
function npc:onTalk(p, answer)
	local quest = QUESTS[questID]

	if quest:availableTo(p) then
		if answer == 0 then p:ask(3646, {3213, 3214, 3215})
		elseif answer == 3213 then p:ask(3647, {3216})
		elseif answer == 3214 then p:ask(3647, {3216})
		elseif answer == 3215 then p:ask(3647, {3216})
		elseif answer == 3216 then p:ask(3648, {3217})
		elseif answer == 3217 then p:ask(3649, {3218})
		elseif answer == 3218 then p:ask(3650, {3219, 3220})
		elseif answer == 3219 then p:ask(3651, {3221})
		elseif answer == 3221 then p:ask(3652, {3222})
		elseif answer == 3222 then
			quest:startFor(p, self.id)
			p:compassTo(10297)
			p:endDialog()
		elseif answer == 3220 then p:endDialog()
		end
		return
	end

	if p:questOngoing(questID) then
		p:ask(3644)
		return
	end
end

RegisterNPCDef(npc)
