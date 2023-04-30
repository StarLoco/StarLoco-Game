local npc = Npc(807, 101)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3330)
    end
end

RegisterNPCDef(npc)
