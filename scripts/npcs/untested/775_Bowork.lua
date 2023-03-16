local npc = Npc(775, 1003)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3172, {2794})
    elseif answer == 2794 then p:endDialog()
    end
end

RegisterNPCDef(npc)
