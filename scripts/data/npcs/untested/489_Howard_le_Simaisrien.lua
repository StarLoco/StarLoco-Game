local npc = Npc(489, 80)

npc.colors = {15257193, 14211289, 9971238}
npc.accessories = {0, 0, 777, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2145)
    end
end

RegisterNPCDef(npc)
