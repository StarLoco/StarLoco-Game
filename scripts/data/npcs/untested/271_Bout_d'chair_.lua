local npc = Npc(271, 1207)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1105, {84})
    end
end

RegisterNPCDef(npc)
