local npc = Npc(164, 9045)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(614, {521})
	elseif answer == 521 then p:ask(616, {523, 522})
	elseif answer == 523 then p:ask(617, {524})
	elseif answer == 524 then p:ask(618, {525})
	elseif answer == 525 then p:ask(619, {526, 527})
	elseif answer == 526 then p:endDialog()
	elseif answer == 522 then p:ask(617, {524})
	elseif answer == 524 then p:ask(618, {525})
	elseif answer == 525 then p:ask(619, {526, 527})
	elseif answer == 526 then p:endDialog()
    end
end

RegisterNPCDef(npc)
