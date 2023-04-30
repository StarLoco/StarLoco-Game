local npc = Npc(373, 1046)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1584)
    end
end

RegisterNPCDef(npc)
