local npc = Npc(504, 9045)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2462)
    end
end

RegisterNPCDef(npc)
