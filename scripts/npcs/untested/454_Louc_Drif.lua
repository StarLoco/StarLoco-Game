local npc = Npc(454, 80)

npc.colors = {7568784, 15390311, 2367793}
npc.accessories = {1650, 6741, 945, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1885, {1638})
    elseif answer == 1638 then p:ask(1886, {1639})
    end
end

RegisterNPCDef(npc)