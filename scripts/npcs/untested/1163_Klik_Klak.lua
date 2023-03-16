local npc = Npc(1163, 1047)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7070)
    end
end

RegisterNPCDef(npc)
