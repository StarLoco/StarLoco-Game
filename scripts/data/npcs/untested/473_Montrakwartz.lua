local npc = Npc(473, 51)

npc.gender = 1
npc.colors = {13144550, 11492316, 2514092}
npc.accessories = {0, 0, 2517, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2012, {170})
    end
end

RegisterNPCDef(npc)
