local npc = Npc(265, 80)

npc.colors = {0, 12763842, 12124160}
npc.accessories = {49, 6741, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1106)
    end
end

RegisterNPCDef(npc)
