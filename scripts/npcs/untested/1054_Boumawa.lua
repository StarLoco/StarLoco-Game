local npc = Npc(1054, 121)

npc.gender = 1
npc.accessories = {0, 2411, 2414, 35, 9025}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5774, {4981})
    end
end

RegisterNPCDef(npc)
