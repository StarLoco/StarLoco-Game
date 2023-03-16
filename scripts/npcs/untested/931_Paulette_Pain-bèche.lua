local npc = Npc(931, 71)

npc.gender = 1
npc.accessories = {0, 8821, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4148)
    end
end

RegisterNPCDef(npc)
