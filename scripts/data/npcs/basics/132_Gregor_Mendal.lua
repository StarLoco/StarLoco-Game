local npc = Npc(132, 9050)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(457, {386})
    elseif answer == 386 then p:ask(458)
    end
end

RegisterNPCDef(npc)
