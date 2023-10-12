local npc = Npc(357, 9041)

npc.colors = {6710886, 0, 12977422}
npc.accessories = {58, 2095, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1446)
    end
end

RegisterNPCDef(npc)
