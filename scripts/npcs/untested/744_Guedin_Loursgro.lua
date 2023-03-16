local npc = Npc(744, 120)

npc.colors = {6364386, 15820425, 16777215}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3092, {2735, 2736})
    elseif answer == 2736 then p:endDialog()
    elseif answer == 2735 then p:ask(3093)
    end
end

RegisterNPCDef(npc)
