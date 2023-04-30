local npc = Npc(292, 1207)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4523)
    end
end

RegisterNPCDef(npc)
