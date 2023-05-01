local npc = Npc(391, 31)

npc.gender = 1
npc.accessories = {0, 0, 2473, 1728, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1632, {}, "[name]")
    end
end

RegisterNPCDef(npc)
