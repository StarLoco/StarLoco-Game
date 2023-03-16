local npc = Npc(667, 120)

npc.colors = {4565957, 4568819, 13627644}
npc.accessories = {0, 6813, 0, 0, 7083}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2738)
    end
end

RegisterNPCDef(npc)
