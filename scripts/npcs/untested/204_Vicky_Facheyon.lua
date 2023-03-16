local npc = Npc(204, 21)

npc.gender = 1
npc.accessories = {0, 712, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1583)
    end
end

RegisterNPCDef(npc)
