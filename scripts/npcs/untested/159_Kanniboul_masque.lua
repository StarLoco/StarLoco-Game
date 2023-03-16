local npc = Npc(159, 9063)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(566, {493})
    end
end

RegisterNPCDef(npc)
