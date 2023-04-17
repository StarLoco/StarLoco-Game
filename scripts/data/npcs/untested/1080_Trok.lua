local npc = Npc(1080, 30)

npc.accessories = {0, 9410, 9411, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5935)
    end
end

RegisterNPCDef(npc)
