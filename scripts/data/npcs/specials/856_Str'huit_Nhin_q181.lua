local npc = Npc(856, 30)
--TODO: Lié à la quête 181
npc.colors = {16711680, 2949120, 1245184}
npc.accessories = {0, 0xbd, 0, 0, 0}
npc.customArtwork = 9097

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3646, {3213, 3214, 3215})
    elseif answer == 3213 then p:ask(3647, {3216})
    elseif answer == 3214 then p:ask(3647, {3216})
	elseif answer == 3214 then p:ask(3647, {3216})
	elseif answer == 3216 then p:ask(3648, {3217})
	elseif answer == 3217 then p:ask(3649, {3218})
	elseif answer == 3218 then p:ask(3650, {3219, 3220})
	elseif answer == 3219 then p:ask(3651, {3221})
	elseif answer == 3221 then p:ask(3652, {3222})
	elseif answer == 3222 then p:ask() --TODO QUEST
	elseif answser == 3220 then p:ask()
    end
end

RegisterNPCDef(npc)
