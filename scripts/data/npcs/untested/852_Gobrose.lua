local npc = Npc(852, 9090)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3632)
    end
end

RegisterNPCDef(npc)
