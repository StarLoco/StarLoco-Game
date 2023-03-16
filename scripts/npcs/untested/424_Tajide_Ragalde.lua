local npc = Npc(424, 81)

npc.gender = 1
npc.colors = {7496947, 15007744, 15660028}
npc.accessories = {0, 1908, 1910, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
    end
end

RegisterNPCDef(npc)
