local npc = Npc(122, 9041)

npc.colors = {1447446, 3553666, 4291938}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(394, {315})
    elseif answer == 315 then p:ask(395)
    end
end

RegisterNPCDef(npc)
