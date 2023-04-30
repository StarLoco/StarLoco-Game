local npc = Npc(740, 40)
--TODO: Lié à la quête 139
npc.accessories = {0, 0, 0, 1711, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3053, {2721, 2722})
    elseif answer == 2722 then p:endDialog()
    end
end

RegisterNPCDef(npc)
