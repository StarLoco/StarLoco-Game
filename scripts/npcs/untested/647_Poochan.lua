local npc = Npc(647, 9001)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2612, {222})
    end
end

RegisterNPCDef(npc)
