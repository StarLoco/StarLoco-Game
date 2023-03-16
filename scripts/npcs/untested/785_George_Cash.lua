local npc = Npc(785, 30)

npc.colors = {14030140, 16439008, 15725307}
npc.accessories = {0, 7516, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3234, {2874})
    elseif answer == 2874 then p:endDialog()
    end
end

RegisterNPCDef(npc)
