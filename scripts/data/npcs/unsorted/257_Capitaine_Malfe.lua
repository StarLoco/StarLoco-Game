local npc = Npc(257, 1197)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(991)
    end
end

RegisterNPCDef(npc)
