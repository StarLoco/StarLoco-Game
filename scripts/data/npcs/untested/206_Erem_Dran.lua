local npc = Npc(206, 9013)

npc.gender = 1
npc.colors = {13816530, 14390681, 12763842}
npc.accessories = {0, 696, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1238)
    end
end

RegisterNPCDef(npc)
