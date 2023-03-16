local npc = Npc(217, 9070)

npc.gender = 1
npc.colors = {983193, 15584670, 14199123}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1556)
    end
end

RegisterNPCDef(npc)
