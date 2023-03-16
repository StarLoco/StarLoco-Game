local npc = Npc(772, 30)

npc.accessories = {0, 6930, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3162, {2780}, "[name]")
    elseif answer == 2780 then p:ask(3163, {2781})
    end
end

RegisterNPCDef(npc)
