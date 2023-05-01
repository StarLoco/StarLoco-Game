local npc = Npc(380, 1150)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1619)
    end
end

RegisterNPCDef(npc)
