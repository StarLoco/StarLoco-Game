local npc = Npc(157, 9046)

local price = 200
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(550, {466, 464, 465, 482})
    elseif answer == 464 then p:ask(551, {468})
    elseif answer == 468 then
        p:ask(555, {469})
    elseif answer == 469 then
        local responses = p:kamas() >= price and {505} or {}
        p:ask(556, responses)
    elseif answer == 505 then
        if p:modKamas(-price) then
            p:addItem(1004)
            p:ask(600)
        end
    elseif answer == 465 then p:ask(552, {471})
    elseif answer == 471 then
        p:ask(558, 472)
    elseif answer == 472 then
        p:ask(559)
    elseif answer == 467 then
        p:ask(554)
    end
end

RegisterNPCDef(npc)
