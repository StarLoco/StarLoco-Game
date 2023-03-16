local npc = Npc(780, 9032)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3178, {2802})
    elseif answer == 2802 then p:endDialog()
    end
end

RegisterNPCDef(npc)
