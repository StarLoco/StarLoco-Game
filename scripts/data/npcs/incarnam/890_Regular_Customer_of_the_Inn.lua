local npc = Npc(890, 9047)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3849)
    end
end

RegisterNPCDef(npc)
