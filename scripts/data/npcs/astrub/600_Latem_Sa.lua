local npc = Npc(600, 20)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2411)
    end
end

RegisterNPCDef(npc)
