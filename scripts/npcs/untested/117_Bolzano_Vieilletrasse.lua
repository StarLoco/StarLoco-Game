local npc = Npc(117, 9045)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(382, {308})
    elseif answer == 308 then p:ask(383)
    end
end

RegisterNPCDef(npc)