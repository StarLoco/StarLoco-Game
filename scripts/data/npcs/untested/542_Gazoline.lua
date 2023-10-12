local npc = Npc(542, 9093)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2309)
    end
end

RegisterNPCDef(npc)
