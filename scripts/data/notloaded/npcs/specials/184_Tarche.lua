local npc = Npc(184, 1191)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(923)
    end
end

RegisterNPCDef(npc)
