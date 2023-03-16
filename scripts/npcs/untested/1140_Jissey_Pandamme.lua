local npc = Npc(1140, 120)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6780)
    end
end

RegisterNPCDef(npc)
