local npc = Npc(1110, 60)

npc.colors = {8529598, 6628251, 10189142}
npc.accessories = {1030, 6952, 6800, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6458)
    end
end

RegisterNPCDef(npc)
