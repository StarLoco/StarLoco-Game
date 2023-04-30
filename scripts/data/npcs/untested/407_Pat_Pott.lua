local npc = Npc(407, 9016)

npc.colors = {15855980, 14400316, 15102315}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1677, {129})
    end
end

RegisterNPCDef(npc)
