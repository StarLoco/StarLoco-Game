local npc = Npc(833, 1012)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4095)
    end
end

RegisterNPCDef(npc)
