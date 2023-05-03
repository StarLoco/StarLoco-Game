local npc = Npc(251, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1075)
    end
end

RegisterNPCDef(npc)
