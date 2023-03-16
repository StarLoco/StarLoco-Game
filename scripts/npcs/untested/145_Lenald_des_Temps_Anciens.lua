local npc = Npc(145, 9057)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(471)
    end
end

RegisterNPCDef(npc)
