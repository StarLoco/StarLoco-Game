local npc = Npc(430, 61)

npc.gender = 1
npc.colors = {16773874, 16775417, 3750201}
npc.accessories = {0, 0, 759, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
    end
end

RegisterNPCDef(npc)
