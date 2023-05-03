local npc = Npc(229, 40)

npc.colors = {8388615, 3547939, 8419447}
npc.accessories = {0, 0, 778, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1117)
    end
end

RegisterNPCDef(npc)
