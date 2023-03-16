local npc = Npc(640, 9051)

npc.accessories = {0, 969, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2744, {2366})
    elseif answer == 2366 then p:endDialog()
    end
end

RegisterNPCDef(npc)
