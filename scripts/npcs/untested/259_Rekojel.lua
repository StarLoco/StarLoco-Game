local npc = Npc(259, 9025)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1123, {805, 806})
    elseif answer == 805 then p:ask(1141)
    elseif answer == 806 then p:endDialog()
    end
end

RegisterNPCDef(npc)
