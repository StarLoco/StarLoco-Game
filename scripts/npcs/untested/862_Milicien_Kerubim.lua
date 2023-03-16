local npc = Npc(862, 9019)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3693)
    end
end

RegisterNPCDef(npc)
