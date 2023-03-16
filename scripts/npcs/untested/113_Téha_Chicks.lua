local npc = Npc(113, 9014)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(376, {306})
    elseif answer == 306 then p:ask(377)
    end
end

RegisterNPCDef(npc)
