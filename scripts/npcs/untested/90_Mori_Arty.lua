local npc = Npc(90, 9023)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(278, {22})
    end
end

RegisterNPCDef(npc)
