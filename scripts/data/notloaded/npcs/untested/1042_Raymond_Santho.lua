local npc = Npc(1042, 9045)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5546)
    end
end

RegisterNPCDef(npc)
