local npc = Npc(650, 9045)

npc.colors = {0, 5720635, 1245184}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2633)
    end
end

RegisterNPCDef(npc)
