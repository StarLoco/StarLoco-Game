local npc = Npc(120, 9041)

npc.colors = {2099287, 6241306, 11635033}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(389)
    end
end

RegisterNPCDef(npc)
