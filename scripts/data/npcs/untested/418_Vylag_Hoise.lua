local npc = Npc(418, 9045)

npc.accessories = {0, 2410, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2472)
    end
end

RegisterNPCDef(npc)
