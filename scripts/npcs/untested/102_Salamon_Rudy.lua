local npc = Npc(102, 9041)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(345)
    end
end

RegisterNPCDef(npc)