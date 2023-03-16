local npc = Npc(156, 9060)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(548, {1596, 463, 488})
    elseif answer == 1596 then p:endDialog()
    elseif answer == 463 then p:endDialog()
    end
end

RegisterNPCDef(npc)