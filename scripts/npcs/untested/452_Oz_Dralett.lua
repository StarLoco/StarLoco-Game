local npc = Npc(452, 90)

npc.colors = {16463414, 3286897, 657930}
npc.accessories = {0, 0, 945, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1878)
    end
end

RegisterNPCDef(npc)
