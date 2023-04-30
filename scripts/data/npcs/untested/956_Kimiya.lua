local npc = Npc(956, 90)

npc.accessories = {0, 8636, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4232)
    end
end

RegisterNPCDef(npc)
