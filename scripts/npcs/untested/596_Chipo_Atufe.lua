local npc = Npc(596, 30)

npc.colors = {14330916, 2566968, 15000282}

npc.sales = {
    {item=497}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2399, {2028, 2027})
    elseif answer == 2027 then p:ask(2400)
    elseif answer == 2028 then p:ask(2401, {2036, 2035})
    end
end

RegisterNPCDef(npc)
