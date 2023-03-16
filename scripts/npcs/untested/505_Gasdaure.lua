local npc = Npc(505, 31)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2214)
    end
end

RegisterNPCDef(npc)
