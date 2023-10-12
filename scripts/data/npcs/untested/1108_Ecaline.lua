local npc = Npc(1108, 61)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6443)
    end
end

RegisterNPCDef(npc)
