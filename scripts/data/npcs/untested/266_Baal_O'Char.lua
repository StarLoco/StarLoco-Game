local npc = Npc(266, 20)

npc.colors = {15371034, 3737856, 16134696}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1103)
    end
end

RegisterNPCDef(npc)
