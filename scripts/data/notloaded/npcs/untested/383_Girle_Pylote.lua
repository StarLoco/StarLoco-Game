local npc = Npc(383, 9071)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1626)
    end
end

RegisterNPCDef(npc)
