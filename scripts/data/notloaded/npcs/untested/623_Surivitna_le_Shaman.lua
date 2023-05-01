local npc = Npc(623, 9075)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2568)
    end
end

RegisterNPCDef(npc)
