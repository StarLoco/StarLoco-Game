local npc = Npc(197, 9067)

npc.colors = {0, 15655990, 14945560}
npc.accessories = {0, 0, 0, 1714, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(989, {68})
    end
end

RegisterNPCDef(npc)
