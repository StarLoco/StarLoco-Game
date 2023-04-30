local npc = Npc(225, 9041)

npc.colors = {10041400, 5460868, 7904185}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1240)
	end
end

RegisterNPCDef(npc)
