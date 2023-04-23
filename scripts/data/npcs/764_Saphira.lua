local npc = Npc(764, 1357)

npc.colors = {7579108, 7579108, 7579108}

---@param p Player
function npc:onTalk(p)
    p:ask(3159)
end

RegisterNPCDef(npc)
