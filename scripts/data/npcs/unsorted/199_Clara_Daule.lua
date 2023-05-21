local npc = Npc(199, 71)

npc.gender = 1
npc.colors = {8568559, 9028071, 16565877}
npc.accessories = {0, 0, 957, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1226)
    end
end

RegisterNPCDef(npc)
