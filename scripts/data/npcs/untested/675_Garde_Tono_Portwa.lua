local npc = Npc(675, 120)

npc.accessories = {0, 6863, 0, 0, 7234}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2803)
    end
end

RegisterNPCDef(npc)
