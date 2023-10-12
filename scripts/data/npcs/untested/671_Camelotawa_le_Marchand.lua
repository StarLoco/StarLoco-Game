local npc = Npc(671, 120)

npc.accessories = {0, 701, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2748)
    end
end

RegisterNPCDef(npc)
