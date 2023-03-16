local npc = Npc(621, 120)

npc.accessories = {0, 6863, 0, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2495, {2143, 2146, 2144})
    elseif answer == 2144 then p:ask(2497, {2145})
    elseif answer == 2146 then p:ask(2499, {4840})
    elseif answer == 2143 then p:ask(2496, {4840})
    end
end

RegisterNPCDef(npc)
