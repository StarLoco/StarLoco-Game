local npc = Npc(166, 9039)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(620, {528})
    elseif answer == 528 then p:ask(621, {529})
    elseif answer == 529 then p:ask(623)
    end
end

RegisterNPCDef(npc)
