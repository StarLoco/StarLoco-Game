local npc = Npc(161, 9062)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(591, {490})
    elseif answer == 490 then p:ask(592)
    end
end

RegisterNPCDef(npc)