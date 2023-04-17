local npc = Npc(146, 1056)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(472, {398})
    end
end

RegisterNPCDef(npc)
