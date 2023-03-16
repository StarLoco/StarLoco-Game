local npc = Npc(328, 1014)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1291)
    end
end

RegisterNPCDef(npc)
