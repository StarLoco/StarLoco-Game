local npc = Npc(820, 9069)

npc.gender = 1
npc.colors = {14966993, 16448004, 470007}
npc.accessories = {0, 0, 0, 6717, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3432)
    end
end

RegisterNPCDef(npc)
