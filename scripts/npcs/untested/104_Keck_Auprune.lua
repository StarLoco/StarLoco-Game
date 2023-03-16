local npc = Npc(104, 9045)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(354)
    end
end

RegisterNPCDef(npc)