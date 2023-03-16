local npc = Npc(755, 1356)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3121, {2744})
    elseif answer == 2744 then p:ask(3122, {2745})
    end
end

RegisterNPCDef(npc)