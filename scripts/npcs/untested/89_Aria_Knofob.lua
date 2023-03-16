local npc = Npc(89, 9013)

npc.gender = 1
npc.colors = {16305964, 3689830, 11171136}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(258, {1005, 1141})
    elseif answer == 1141 then p:ask(259)
    elseif answer == 1005 then p:ask(260, {336})
    end
end

RegisterNPCDef(npc)
