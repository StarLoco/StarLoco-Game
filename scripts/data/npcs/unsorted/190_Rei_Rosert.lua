local npc = Npc(190, 9046)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(940)
    end
end

RegisterNPCDef(npc)
