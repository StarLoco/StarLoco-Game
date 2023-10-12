local npc = Npc(415, 9069)

npc.gender = 1
npc.colors = {16777215, 16709855, 16767501}
npc.accessories = {0, 2485, 2489, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2473)
    end
end

RegisterNPCDef(npc)
