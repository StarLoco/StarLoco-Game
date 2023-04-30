local npc = Npc(284, 9042)

npc.gender = 1
npc.colors = {5404089, 10033176, 11539617}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1108)
    end
end

RegisterNPCDef(npc)
