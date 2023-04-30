local npc = Npc(769, 9048)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3155)
    end
end

RegisterNPCDef(npc)
