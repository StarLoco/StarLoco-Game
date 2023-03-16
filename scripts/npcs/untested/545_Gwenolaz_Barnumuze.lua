local npc = Npc(545, 21)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2328, {1932})
    elseif answer == 1932 then p:ask(2329)
    end
end

RegisterNPCDef(npc)
