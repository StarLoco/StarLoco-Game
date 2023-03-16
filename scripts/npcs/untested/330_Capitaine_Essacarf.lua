local npc = Npc(330, 1197)

npc.accessories = {0, 2097, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1288, {248})
    elseif answer == 248 then p:ask(304, {240, 246})
    elseif answer == 240 then p:ask(305, {247, 249})
    elseif answer == 247 then p:ask(307)
    elseif answer == 249 then p:ask(306)
    elseif answer == 246 then p:ask(307)
    end
end

RegisterNPCDef(npc)
