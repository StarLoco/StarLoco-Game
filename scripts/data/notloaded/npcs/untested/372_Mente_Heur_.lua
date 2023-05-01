local npc = Npc(372, 9047)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1564)
    end
end

RegisterNPCDef(npc)
