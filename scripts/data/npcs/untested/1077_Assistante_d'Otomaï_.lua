local npc = Npc(1077, 31)

npc.gender = 1
npc.colors = {14289675, 9127098, 14408482}
npc.accessories = {347, 6989, 8650, 7911, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6122, {530})
    end
end

RegisterNPCDef(npc)
