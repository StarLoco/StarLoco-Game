local npc = Npc(358, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1457)
    end
end

RegisterNPCDef(npc)
