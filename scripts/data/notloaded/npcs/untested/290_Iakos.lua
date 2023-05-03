local npc = Npc(290, 9045)

npc.accessories = {1475, 703, 954, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1112)
    end
end

RegisterNPCDef(npc)
