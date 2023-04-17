local npc = Npc(609, 110)

npc.accessories = {0, 702, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2468)
    end
end

RegisterNPCDef(npc)
