local npc = Npc(882, 9003)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3697)
    end
end

RegisterNPCDef(npc)
