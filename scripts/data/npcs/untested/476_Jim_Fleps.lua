local npc = Npc(476, 40)

npc.colors = {9839144, 10658466, 9811151}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1978)
    end
end

RegisterNPCDef(npc)
