local npc = Npc(448, 9064)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1840, {1746})
    elseif answer == 1746 then p:ask(2069)
    end
end

RegisterNPCDef(npc)