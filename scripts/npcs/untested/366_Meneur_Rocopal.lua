local npc = Npc(366, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1503)
    end
end

RegisterNPCDef(npc)
