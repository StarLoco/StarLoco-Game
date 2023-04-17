local npc = Npc(406, 9016)

npc.colors = {16755963, 15410549, 15607978}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1681, {1299, 129})
    elseif answer == 1299 then p:endDialog()
    end
end

RegisterNPCDef(npc)
