local npc = Npc(700, 120)

npc.colors = {15918981, 15903590, 14508129}
npc.accessories = {0, 6863, 0, 0, 7236}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2885)
    end
end

RegisterNPCDef(npc)
