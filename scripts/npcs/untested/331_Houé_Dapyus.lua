local npc = Npc(331, 9004)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1576)
    end
end

RegisterNPCDef(npc)
