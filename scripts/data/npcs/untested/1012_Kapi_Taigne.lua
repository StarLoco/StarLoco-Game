local npc = Npc(1012, 1205)

npc.accessories = {0, 0, 2118, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5117)
    end
end

RegisterNPCDef(npc)
