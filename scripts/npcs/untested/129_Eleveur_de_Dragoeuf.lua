local npc = Npc(129, 9003)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(433, {359})
    elseif answer == 359 then p:ask(434, {360})
    end
end

RegisterNPCDef(npc)
