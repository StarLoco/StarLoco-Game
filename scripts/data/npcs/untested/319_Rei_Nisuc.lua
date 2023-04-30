local npc = Npc(319, 1207)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1272)
    end
end

RegisterNPCDef(npc)
