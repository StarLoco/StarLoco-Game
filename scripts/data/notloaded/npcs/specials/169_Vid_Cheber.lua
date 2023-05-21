local npc = Npc(169, 9039)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(630, {534})
    end
end

RegisterNPCDef(npc)
