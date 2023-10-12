local npc = Npc(954, 120)

npc.accessories = {0, 8989, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4226, {371})
    elseif answer == 371 then p:ask(444)
    end
end

RegisterNPCDef(npc)
