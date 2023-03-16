local npc = Npc(401, 9069)

npc.gender = 1
npc.colors = {16727891, 16768993, 16711693}
npc.accessories = {0, 2447, 2446, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1642, {127})
    elseif answer == 127 then p:ask(164)
    end
end

RegisterNPCDef(npc)
