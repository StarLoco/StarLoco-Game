local npc = Npc(763, 1357)

npc.gender = 1
npc.colors = {4048569, 4048569, 4048569}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3148, {2769})
    end
end

RegisterNPCDef(npc)
