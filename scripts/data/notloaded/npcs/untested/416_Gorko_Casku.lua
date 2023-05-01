local npc = Npc(416, 9041)

npc.colors = {7878969, 12154386, 16764249}
npc.accessories = {40, 709, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1687)
    end
end

RegisterNPCDef(npc)
