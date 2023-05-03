local npc = Npc(904, 1109)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3984)
    end
end

RegisterNPCDef(npc)
