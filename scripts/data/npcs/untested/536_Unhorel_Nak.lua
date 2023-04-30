local npc = Npc(536, 80)

npc.colors = {0, 16591636, 13814213}
npc.accessories = {0, 6863, 6886, 0, 7069}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2279, {1911})
    elseif answer == 1911 then p:ask(2280, {1912})
    elseif answer == 1912 then p:ask(2281, {1913})
    elseif answer == 1913 then p:ask(2282, {1914})
    elseif answer == 1914 then p:ask(2283)
    end
end

RegisterNPCDef(npc)
