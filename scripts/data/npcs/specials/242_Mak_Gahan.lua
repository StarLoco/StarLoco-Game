local npc = Npc(242, 1014)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1042)
    end
end

RegisterNPCDef(npc)
