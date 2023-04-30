local npc = Npc(363, 1211)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1464, {4840})
    elseif answer == 4840 then p:endDialog()
    end
end

RegisterNPCDef(npc)
