local npc = Npc(261, 10)

npc.colors = {0, 16184312, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1114)
    end
end

RegisterNPCDef(npc)
