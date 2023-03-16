local npc = Npc(709, 9075)

npc.accessories = {0, 7146, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2943, {2577})
    end
end

RegisterNPCDef(npc)
