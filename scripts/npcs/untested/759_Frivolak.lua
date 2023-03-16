local npc = Npc(759, 1363)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3142, {2765})
    elseif answer == 2765 then p:endDialog()
    end
end

RegisterNPCDef(npc)