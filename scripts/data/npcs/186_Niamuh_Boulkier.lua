local npc = Npc(186, 9049)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(950, {681, 673})
    elseif answer == 673 then p:ask(951, {677, 678, 679, 680})
    elseif answer == 677 then p:ask(952) p:compassTo(4308)
    elseif answer == 678 then p:ask(953) p:compassTo(4079)
    elseif answer == 679 then p:ask(954) p:compassTo(4221)
    elseif answer == 680 then p:ask(955) p:compassTo(4223)
    elseif answer == 681 then p:ask(960, {682, 683})
    elseif answer == 682 then p:ask(961)
    elseif answer == 683 then p:ask(962)
    end
end

RegisterNPCDef(npc)
