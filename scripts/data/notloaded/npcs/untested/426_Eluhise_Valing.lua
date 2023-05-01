local npc = Npc(426, 91)

npc.gender = 1
npc.accessories = {0, 708, 744, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1733, {1366, 1377})
    elseif answer == 1377 then p:ask(1743)
    elseif answer == 1366 then p:ask(1729)
    end
end

RegisterNPCDef(npc)
