local npc = Npc(474, 70)

npc.colors = {8923713, 15453135, 14462638}
npc.accessories = {0, 0, 2061, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2140)
    end
end

RegisterNPCDef(npc)
