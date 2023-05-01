local npc = Npc(343, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1391)
    end
end

RegisterNPCDef(npc)
