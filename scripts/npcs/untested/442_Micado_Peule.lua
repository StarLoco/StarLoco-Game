local npc = Npc(442, 100)

npc.colors = {328193, 12226652, 6459324}
npc.accessories = {0, 0, 772, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1770, {6697, 6772, 1419, 1509})
    elseif answer == 6772 then p:ask(7136, {6770, 6771})
    elseif answer == 1509 then p:ask(1793, {1513})
    elseif answer == 6697 then p:ask(7108, {6701, 6699, 6500, 7326, 6599, 6768})
    elseif answer == 1419 then p:ask(205)
    end
end

RegisterNPCDef(npc)
