local npc = Npc(1216, 9212)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7544, {7462, 7463})
    elseif answer == 7462 then
        p:teleport(12227, 269)
        p:endDialog()
    elseif answer == 7463 then
        p:endDialog()
    end

end

RegisterNPCDef(npc)
