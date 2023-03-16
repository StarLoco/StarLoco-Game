local npc = Npc(451, 9043)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1910, {1856})
    elseif answer == 1856 then p:endDialog()
    end
end

RegisterNPCDef(npc)