local npc = Npc(867, 9050)

npc.colors = {4159935, 3436746, 6176192}
npc.accessories = {0, 0, 0, 8151, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3705)
    end
end

RegisterNPCDef(npc)
