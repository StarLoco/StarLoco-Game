local npc = Npc(449, 9045)
--TODO: Lié à la quête 3
npc.colors = {15263977, 9148870, 4860956}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1841, {1637, 1636})
    elseif answer == 1636 then p:ask(2219, {1727})
    elseif answer == 1727 then p:endDialog()
    elseif answer == 1637 then p:ask(1881)
    end
end

RegisterNPCDef(npc)
