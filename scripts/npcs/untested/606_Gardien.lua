local npc = Npc(606, 1211)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2450, {2114, 2111, 2551, 2554, 2112, 2113, 2108, 2553, 2550, 2109, 2552})
    elseif answer == 2551 then p:ask(2906)
    end
end

RegisterNPCDef(npc)
