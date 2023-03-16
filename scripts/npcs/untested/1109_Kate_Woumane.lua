local npc = Npc(1109, 21)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6444)
    end
end

RegisterNPCDef(npc)
