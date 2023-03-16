local npc = Npc(155, 9063)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(546, {3933})
    end
end

RegisterNPCDef(npc)
