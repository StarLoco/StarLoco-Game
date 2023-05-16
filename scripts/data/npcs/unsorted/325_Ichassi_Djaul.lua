local npc = Npc(325, 9012)

npc.gender = 1
npc.colors = {2244632, 8493368, 4157486}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1289)
    end
end

RegisterNPCDef(npc)
