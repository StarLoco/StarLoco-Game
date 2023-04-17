local npc = Npc(801, 9047)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3300)
    end
end

RegisterNPCDef(npc)
