local npc = Npc(764, 1357)

npc.colors = {7579108, 7579108, 7579108}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    p:ask(3159)
end

RegisterNPCDef(npc)
