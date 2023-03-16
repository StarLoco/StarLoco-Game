local npc = Npc(233, 9045)

npc.accessories = {0, 1908, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(918)
    end
end

RegisterNPCDef(npc)
