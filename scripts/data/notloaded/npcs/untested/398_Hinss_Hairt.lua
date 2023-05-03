local npc = Npc(398, 9016)

npc.colors = {16777215, 16777215, 16777215}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1648, {130})
    elseif answer == 130 then p:ask(145, {124, 126, 127, 128})
    elseif answer == 128 then p:ask(164)
    elseif answer == 124 then p:ask(140)
    elseif answer == 126 then p:ask(164)
    elseif answer == 127 then p:ask(164)
    end
end

RegisterNPCDef(npc)
