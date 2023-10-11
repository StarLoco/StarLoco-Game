local npc = Npc(733, 6000)

npc.colors = {10384730, 10262159, 10452831}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3034)
    end
end

RegisterNPCDef(npc)
