local npc = Npc(705, 1295)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2938, {2575})
    end
end

RegisterNPCDef(npc)