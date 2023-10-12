local npc = Npc(1228, 1073)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7583, {758})
    elseif answer == 758 then p:endDialog()
    end
end

RegisterNPCDef(npc)
