local npc = Npc(214, 9049)

npc.colors = {11608096, -1, -1}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(950, {681, 673})
	elseif answer == 681 then p:ask(960, {682, 683})
    elseif answer == 682 then
		p:compassTo(4222)
		p:ask(961)
    elseif answer == 683 then p:ask(962)
    elseif answer == 673 then p:ask(951, {677, 678, 679, 680})
    elseif answer == 677 then
		p:compassTo(4308)
		p:ask(952) 
    elseif answer == 678 then
		p:compassTo(4079)
		p:ask(953) 
    elseif answer == 679 then
		p:compassTo(4221)
		p:ask(954) 
    elseif answer == 680 then
		p:compassTo(4223)
		p:ask(955) 
    end
end

RegisterNPCDef(npc)
