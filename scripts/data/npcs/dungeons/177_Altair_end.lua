local npc = Npc(177, 9059)

npc.colors = {6380875, 16777215, 11250604}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(677, {605})
    elseif answer == 605 then
        p:addItem(1728)
        p:teleport(1856, 226)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
