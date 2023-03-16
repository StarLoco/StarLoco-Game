local npc = Npc(732, 9000)

npc.colors = {15340564, 15721231, 16777215}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3016, {2662})
    elseif answer == 2662 then p:ask(3017, {2663, 2664})
    end
end

RegisterNPCDef(npc)