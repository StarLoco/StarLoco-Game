local npc = Npc(230, 9041)

npc.colors = {8616570, 8220225, 5649443}
npc.accessories = {0, 705, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1113)
    end
end

RegisterNPCDef(npc)
