local npc = Npc(428, 41)

npc.gender = 1
npc.colors = {15007744, 1644825, 14992064}
npc.accessories = {0, 0, 0, 1728, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
    end
end
--TODO: check offi en étant féca
RegisterNPCDef(npc)