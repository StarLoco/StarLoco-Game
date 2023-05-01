local npc = Npc(207, 100)

npc.colors = {3560987, 12696766, 12823162}
npc.accessories = {0, 710, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1221)
    end
end

RegisterNPCDef(npc)
