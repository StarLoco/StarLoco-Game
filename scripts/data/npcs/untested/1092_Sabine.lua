local npc = Npc(1092, 71)

npc.gender = 1
npc.accessories = {0, 8116, 8117, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6249)
    end
end

RegisterNPCDef(npc)
