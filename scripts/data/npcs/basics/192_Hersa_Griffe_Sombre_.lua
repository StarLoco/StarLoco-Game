local npc = Npc(192, 61)

npc.gender = 1
npc.accessories = {1530, 703, 777, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6420)
    end
end

RegisterNPCDef(npc)
