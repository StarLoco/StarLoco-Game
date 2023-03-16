local npc = Npc(194, 1191)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(930)
    end
end

RegisterNPCDef(npc)
