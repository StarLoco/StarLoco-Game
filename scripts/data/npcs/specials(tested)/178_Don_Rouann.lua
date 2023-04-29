local npc = Npc(178, 9045)

npc.colors = {15316858, 16107743, 6833254}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(681, {618})
    elseif answer == 618 then p:ask(682, {619})
    elseif answer == 619 then
        if p:getItem(1653) then
            p:ask(683, {620})
        else
            p:addItem(1653)
            p:ask(683, {620})
        end
    elseif answer == 620 then p:ask(684, {})
    end
end

RegisterNPCDef(npc)
