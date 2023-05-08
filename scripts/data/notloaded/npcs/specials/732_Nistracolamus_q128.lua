local npc = Npc(732, 9000)
--TODO: Lié à la quête 128
npc.colors = {15340564, 15721231, 16777215}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3016, {2662})
    elseif answer == 2662 then p:ask(3017, {2663, 2664})
    elseif answer == 2663 then p:ask(3018, {2667, 2666})
    elseif answer == 2666 then p:endDialog()
    elseif answer == 2667 then p:endDialog()
    elseif answer == 2664 then p:ask(3019, {2665})
    elseif answer == 2665 then p:ask(3018, {2667, 2666})
    end
end

RegisterNPCDef(npc)
