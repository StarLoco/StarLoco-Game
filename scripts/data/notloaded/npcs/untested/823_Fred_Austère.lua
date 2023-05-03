local npc = Npc(823, 80)

npc.colors = {11358362, 16777139, 0}
npc.accessories = {235, 0, 772, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3436)
    end
end

RegisterNPCDef(npc)
