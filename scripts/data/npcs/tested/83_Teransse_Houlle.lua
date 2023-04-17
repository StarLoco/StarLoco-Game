local npc = Npc(83, 9023)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(175)
    end
end

RegisterNPCDef(npc)
