local npc = Npc(964, 9045)

npc.colors = {34, 113, 41}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4345)
    end
end

RegisterNPCDef(npc)
