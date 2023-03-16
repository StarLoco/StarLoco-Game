local npc = Npc(779, 30)

npc.accessories = {0, 6952, 6954, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3177, {2801})
    elseif answer == 2801 then p:endDialog()
    end
end

RegisterNPCDef(npc)
