local npc = Npc(506, 30)

npc.colors = {11448455, -1, 8086352}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2215)
    end
end

RegisterNPCDef(npc)
