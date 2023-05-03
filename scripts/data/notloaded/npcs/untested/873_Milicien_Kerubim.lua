local npc = Npc(873, 9019)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3761)
    end
end

RegisterNPCDef(npc)
