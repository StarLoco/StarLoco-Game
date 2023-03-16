local npc = Npc(926, 10)

npc.accessories = {0, 8848, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4138)
    end
end

RegisterNPCDef(npc)
