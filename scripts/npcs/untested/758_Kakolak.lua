local npc = Npc(758, 1439)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3138, {2762})
    end
end

RegisterNPCDef(npc)