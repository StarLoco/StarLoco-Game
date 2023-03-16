local npc = Npc(937, 110)

npc.accessories = {0, 8843, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4181)
    end
end

RegisterNPCDef(npc)
