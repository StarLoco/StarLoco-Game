local npc = Npc(626, 9075)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2520)
    end
end

RegisterNPCDef(npc)
