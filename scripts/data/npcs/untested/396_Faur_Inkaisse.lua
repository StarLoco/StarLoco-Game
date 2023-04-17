local npc = Npc(396, 9016)

npc.colors = {2654703, 4033774, 13762560}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1686, {1302})
    end
end

RegisterNPCDef(npc)
