local npc = Npc(725, 120)
--TODO: Lié à la quête 126
npc.accessories = {0, 700, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2964, {2596, 2595})
    elseif answer == 2595 then p:endDialog()
    elseif answer == 2596 then p:endDialog()
    end
end

RegisterNPCDef(npc)
