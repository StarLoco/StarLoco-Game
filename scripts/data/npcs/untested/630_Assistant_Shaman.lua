local npc = Npc(630, 1281)

npc.colors = {16773874, 15007744, 16777215}
npc.accessories = {0, 0, 2629, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2525)
    end
end

RegisterNPCDef(npc)
