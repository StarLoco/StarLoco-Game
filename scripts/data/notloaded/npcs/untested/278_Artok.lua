local npc = Npc(278, 9034)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1102)
    end
end

RegisterNPCDef(npc)
