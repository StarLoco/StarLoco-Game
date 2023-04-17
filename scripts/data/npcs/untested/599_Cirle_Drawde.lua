local npc = Npc(599, 70)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2410)
    end
end

RegisterNPCDef(npc)
