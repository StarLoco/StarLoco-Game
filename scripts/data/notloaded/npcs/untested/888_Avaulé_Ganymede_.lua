local npc = Npc(888, 9059)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3840, {3373, 338})
    elseif answer == 338 then p:ask(418)
    end
end

RegisterNPCDef(npc)
