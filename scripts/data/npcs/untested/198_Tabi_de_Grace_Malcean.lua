local npc = Npc(198, 7000)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1558)
    end
end

RegisterNPCDef(npc)
