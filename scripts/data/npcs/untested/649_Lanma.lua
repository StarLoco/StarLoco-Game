local npc = Npc(649, 9051)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2642)
    end
end

RegisterNPCDef(npc)
