local npc = Npc(874, 9013)

npc.gender = 1
npc.colors = {10118252, 15590193, 15395386}
npc.accessories = {0, 0, 0, 1728, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2003)
    end
end

RegisterNPCDef(npc)