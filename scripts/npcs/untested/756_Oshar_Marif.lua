local npc = Npc(756, 10)

npc.accessories = {0, 8009, 8007, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3127, {2754, 2753})
    elseif answer == 2753 then p:ask(3128)
    elseif answer == 2754 then p:ask(3129, {2755})
    end
end

RegisterNPCDef(npc)
