local npc = Npc(103, 9044)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(353)
    end
end

RegisterNPCDef(npc)
