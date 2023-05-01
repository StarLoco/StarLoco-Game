local npc = Npc(662, 121)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2821)
    end
end

RegisterNPCDef(npc)
