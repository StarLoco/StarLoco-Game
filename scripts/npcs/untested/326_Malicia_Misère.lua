local npc = Npc(326, 9013)

npc.gender = 1
npc.colors = {5326768, 2039119, 0}
npc.accessories = {0, 0, 952, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1290)
    end
end

RegisterNPCDef(npc)
