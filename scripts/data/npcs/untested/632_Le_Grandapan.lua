local npc = Npc(632, 9076)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2587)
    end
end

RegisterNPCDef(npc)
