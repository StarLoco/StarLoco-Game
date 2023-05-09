local npc = Npc(1279, 9219)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then
        p:ask(7674, {7695, 7696})
    elseif answer == 7696 then
        p:teleport(1113, 199)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
