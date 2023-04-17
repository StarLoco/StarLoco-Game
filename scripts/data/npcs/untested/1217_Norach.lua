local npc = Npc(1217, 1188)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7546, {7464, 7465})
    elseif answer == 7465 then p:endDialog()
    end
end

RegisterNPCDef(npc)
