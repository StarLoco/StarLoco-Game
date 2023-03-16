local npc = Npc(795, 30)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3258, {2901, 2898})
    elseif answer == 2901 then p:endDialog()
    end
end

RegisterNPCDef(npc)