local npc = Npc(82, 9023)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(174)
    end
end

RegisterNPCDef(npc)
