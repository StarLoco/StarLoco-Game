local npc = Npc(654, 120)

npc.colors = {0, 0, 16777215}
npc.accessories = {0, 941, 0, 0, 7196}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2650, {2259, 2262, 2260})
    elseif answer == 2259 then p:ask(2651)
    elseif answer == 2260 then p:ask(2652)
    elseif answer == 2262 then p:ask(2654)
    end
end

RegisterNPCDef(npc)
