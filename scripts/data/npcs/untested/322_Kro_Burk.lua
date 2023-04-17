local npc = Npc(322, 1003)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1285)
    end
end

RegisterNPCDef(npc)
