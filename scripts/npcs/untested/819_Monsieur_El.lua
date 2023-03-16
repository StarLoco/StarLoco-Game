local npc = Npc(819, 9030)

npc.colors = {1705930, 6650520, 16053303}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3430, {3039, 3040})
    elseif answer == 3040 then p:endDialog()
    elseif answer == 3039 then p:ask(3431)
    end
end

RegisterNPCDef(npc)
