local npc = Npc(615, 30)

npc.accessories = {0, 6764, 744, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2490)
    end
end

RegisterNPCDef(npc)
