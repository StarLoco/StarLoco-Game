local npc = Npc(776, 1012)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3173, {2795})
    elseif answer == 2795 then p:endDialog()
    end
end

RegisterNPCDef(npc)
