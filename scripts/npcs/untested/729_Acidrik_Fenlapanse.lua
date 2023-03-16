local npc = Npc(729, 9023)

npc.colors = {11756874, 16777215, 6249554}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2984, {2615, 2616, 2613})
    elseif answer == 2613 then p:endDialog()
    elseif answer == 2615 then p:endDialog()
    elseif answer == 2616 then p:endDialog()
    end
end

RegisterNPCDef(npc)
