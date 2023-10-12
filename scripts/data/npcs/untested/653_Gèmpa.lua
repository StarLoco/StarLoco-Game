local npc = Npc(653, 9075)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2619)
    end
end

RegisterNPCDef(npc)
