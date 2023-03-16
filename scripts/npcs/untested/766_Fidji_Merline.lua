local npc = Npc(766, 9079)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3154, {2774, 2775}, "[name]")
    elseif answer == 2774 then p:ask(3151, {4840})
    elseif answer == 4840 then p:endDialog()
    elseif answer == 2775 then p:ask(3152)
    end
end

RegisterNPCDef(npc)
