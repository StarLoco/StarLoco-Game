local npc = Npc(971, 9015)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4469)
    end
end

RegisterNPCDef(npc)
