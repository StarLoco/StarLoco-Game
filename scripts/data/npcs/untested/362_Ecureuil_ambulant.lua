local npc = Npc(362, 1046)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1468)
    end
end

RegisterNPCDef(npc)
