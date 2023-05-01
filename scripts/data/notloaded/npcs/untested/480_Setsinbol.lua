local npc = Npc(480, 90)

npc.colors = {1245184, 6460930, 16382289}
npc.accessories = {0, 1090, 2414, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2080)
    end
end

RegisterNPCDef(npc)
