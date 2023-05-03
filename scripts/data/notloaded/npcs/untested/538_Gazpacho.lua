local npc = Npc(538, 9093)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2304)
    end
end

RegisterNPCDef(npc)
