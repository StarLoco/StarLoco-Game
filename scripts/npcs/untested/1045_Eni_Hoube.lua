local npc = Npc(1045, 70)

npc.accessories = {0, 8533, 8534, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5549, {4740, 4739})
    elseif answer == 4739 then p:ask(5625, {4741, 4742})
    elseif answer == 4740 then p:ask(5626, {4744, 4743})
    end
end

RegisterNPCDef(npc)