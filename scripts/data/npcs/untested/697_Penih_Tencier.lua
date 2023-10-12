local npc = Npc(697, 1206)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2949)
    end
end

RegisterNPCDef(npc)
