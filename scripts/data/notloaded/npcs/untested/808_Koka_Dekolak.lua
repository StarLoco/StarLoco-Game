local npc = Npc(808, 120)

npc.colors = {16724541, 2340131, 5658248}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3336)
    end
end

RegisterNPCDef(npc)
