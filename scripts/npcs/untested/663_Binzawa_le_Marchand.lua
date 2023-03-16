local npc = Npc(663, 120)

npc.colors = {15197616, 15854035, 15591613}
npc.accessories = {0, 6938, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2845)
    end
end

RegisterNPCDef(npc)
