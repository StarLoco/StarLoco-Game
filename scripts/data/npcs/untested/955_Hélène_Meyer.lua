local npc = Npc(955, 61)

npc.gender = 1
npc.colors = {16644607, 16511998, -327686}
npc.accessories = {0, 8704, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4229)
    end
end

RegisterNPCDef(npc)
