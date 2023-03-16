local npc = Npc(777, 9031)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3174, {2796})
    elseif answer == 2796 then p:ask(3175, {2797})
    end
end

RegisterNPCDef(npc)
