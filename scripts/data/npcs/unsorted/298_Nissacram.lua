local npc = Npc(298, 9050)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1160)
    end
end

RegisterNPCDef(npc)
