local npc = Npc(660, 120)

npc.colors = {16177887, 15785102, 3119055}
npc.accessories = {0, 6778, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2822)
    end
end

RegisterNPCDef(npc)
