local npc = Npc(706, 1260)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2940, {2576})
    elseif answer == 2576 then p:endDialog()
    end
end

RegisterNPCDef(npc)
