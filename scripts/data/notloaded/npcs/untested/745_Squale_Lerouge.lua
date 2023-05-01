local npc = Npc(745, 70)

npc.colors = {0, 14408521, 16011344}
npc.accessories = {0, 699, 677, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3088)
    end
end

RegisterNPCDef(npc)
