local npc = Npc(423, 101)

npc.gender = 1
npc.colors = {9168159, 1225720, 15958545}
npc.accessories = {0, 6482, 2385, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
    end
end

RegisterNPCDef(npc)
