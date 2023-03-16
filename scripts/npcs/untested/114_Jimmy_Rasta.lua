local npc = Npc(114, 9082)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(378)
    end
end

RegisterNPCDef(npc)