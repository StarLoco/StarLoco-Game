local npc = Npc(137, 9055)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(470, {394, 395, 396})
    elseif answer == 396 then p:endDialog()
    end
end

RegisterNPCDef(npc)