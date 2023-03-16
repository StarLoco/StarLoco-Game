local npc = Npc(123, 9047)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(396, {316})
    elseif answer == 316 then p:ask(397)
    end
end

RegisterNPCDef(npc)