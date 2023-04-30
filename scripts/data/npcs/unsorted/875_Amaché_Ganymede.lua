local npc = Npc(875, 9211)

npc.colors = {8781824, 3642041, 16701568}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3766, {3397, 7659}, "[name]")
    elseif answer == 3397 then p:endDialog()
    elseif answer == 7659 then p:teleport(6825, 252)
    end
end

RegisterNPCDef(npc)