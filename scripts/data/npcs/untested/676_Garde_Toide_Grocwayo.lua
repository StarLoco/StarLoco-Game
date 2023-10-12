local npc = Npc(676, 120)

npc.accessories = {0, 6863, 0, 0, 7235}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2805)
    end
end

RegisterNPCDef(npc)
