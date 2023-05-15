local npc = Npc(162, 9023)

local price = 5000
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(608, {512})
    elseif answer == 512 then
        p:ask(609, {513})
    elseif answer == 513 then
        local responses = p:kamas() >= price and p:getItem(363, 5) and {514} or {}
        p:ask(610, responses)
    elseif answer == 514 then
        if p:modKamas(-price) and p:consumeItem(363, 5) then
            p:addItem(998)
            p:ask(611)
        end
    end
end

RegisterNPCDef(npc)
