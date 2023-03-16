local npc = Npc(258, 61)

npc.gender = 1
npc.colors = {10301633, 8005789, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1109, {111})
    elseif answer == 111 then p:ask(129)
    end
end

RegisterNPCDef(npc)
