local npc = Npc(220, 9013)

npc.gender = 1
npc.accessories = {0, 1019, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(978)
    end
end

RegisterNPCDef(npc)
