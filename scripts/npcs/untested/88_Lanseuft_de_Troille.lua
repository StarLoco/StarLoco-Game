local npc = Npc(88, 9003)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(243, {196, 195, 566})
    elseif answer == 195 then p:ask(3282, {293})
    elseif answer == 196 then p:ask(245, {197})
    elseif answer == 566 then p:ask(663)
    end
end

RegisterNPCDef(npc)