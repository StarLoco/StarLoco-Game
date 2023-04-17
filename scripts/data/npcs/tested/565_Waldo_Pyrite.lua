local npc = Npc(565, 30)

npc.colors = {13618113, -1, -1}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2360)
    end
end

RegisterNPCDef(npc)
