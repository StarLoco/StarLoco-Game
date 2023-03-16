local npc = Npc(487, 9010)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2001)
    end
end

RegisterNPCDef(npc)
