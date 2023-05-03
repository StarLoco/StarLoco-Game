local npc = Npc(425, 31)

npc.gender = 1
npc.accessories = {0, 706, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
    end
end

RegisterNPCDef(npc)
