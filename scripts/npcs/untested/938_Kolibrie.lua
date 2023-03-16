local npc = Npc(938, 1108)

npc.gender = 1
npc.accessories = {0, 8918, 8919, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4182, {3946})
    elseif answer == 3946 then p:ask(4578, {3947})
    elseif answer == 3947 then p:ask(4182, {3946})
    end
end

RegisterNPCDef(npc)
