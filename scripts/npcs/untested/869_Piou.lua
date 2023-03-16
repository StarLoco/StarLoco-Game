local npc = Npc(869, 1212)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3710, {3262, 3260})
    elseif answer == 3260 then p:ask(3723, {3264, 326})
    elseif answer == 3262 then p:ask(3724, {3265, 326})
    end
end

RegisterNPCDef(npc)
