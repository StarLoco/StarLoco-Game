local npc = Npc(756, 10)

npc.accessories = {0, 8009, 8007, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3127, {2754, 2753})
    elseif answer == 2753 then p:ask(3128)
    elseif answer == 2754 then p:ask(3129, {2755})
    elseif answer == 2755 then p:ask(3130, {2756})
    elseif answer == 2756 then p:ask(3131, {2757})
    elseif answer == 2757 then p:ask(3132)
    end
end

RegisterNPCDef(npc)
