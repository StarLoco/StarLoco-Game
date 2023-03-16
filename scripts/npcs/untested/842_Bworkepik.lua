local npc = Npc(842, 1003)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3550, {3114})
    elseif answer == 3114 then p:ask(3551, {3116, 3115})
    end
end

RegisterNPCDef(npc)
