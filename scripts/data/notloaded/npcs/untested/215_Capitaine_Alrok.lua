local npc = Npc(215, 1197)

npc.accessories = {0, 0, 945, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(970)
    end
end

RegisterNPCDef(npc)
