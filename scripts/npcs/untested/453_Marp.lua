local npc = Npc(453, 21)

npc.gender = 1
npc.colors = {16777215, 11475762, 16506051}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1879)
    end
end

RegisterNPCDef(npc)
