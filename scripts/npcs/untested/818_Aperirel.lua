local npc = Npc(818, 111)

npc.gender = 1
npc.accessories = {0, 8457, 8458, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3415, {3032})
    end
end

RegisterNPCDef(npc)