local npc = Npc(702, 9077)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2891)
    end
end

RegisterNPCDef(npc)
