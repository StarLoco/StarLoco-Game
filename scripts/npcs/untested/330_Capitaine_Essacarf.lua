local npc = Npc(330, 1197)

npc.accessories = {0, 2097, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1288, {248})
    elseif answer == 248 then p:ask(304, {240, 246})
    end
end

RegisterNPCDef(npc)