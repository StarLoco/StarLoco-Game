local npc = Npc(285, 9069)

npc.gender = 1
npc.colors = {14582243, 16638180, 14225938}
npc.accessories = {0, 0, 754, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1110)
    end
end

RegisterNPCDef(npc)
