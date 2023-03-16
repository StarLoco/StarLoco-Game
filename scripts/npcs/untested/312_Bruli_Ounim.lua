local npc = Npc(312, 9012)

npc.gender = 1
npc.colors = {14408668, 8468283, 6039595}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1230, {92})
    end
end

RegisterNPCDef(npc)
