local npc = Npc(1010, 9006)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5116)
    end
end

RegisterNPCDef(npc)
