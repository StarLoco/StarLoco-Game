local npc = Npc(134, 9053)

npc.gender = 1

local price = 500
function npc:onTalk(p, answer)
    if answer == 0 then
        local responses = p:kamas() >= price and {387} or {}
        p:ask(460, responses)
    elseif answer == 387 then
        if p:modKamas(-price) then
            p:sendAction(-1, 2, "2")
            p:teleport(833, 141)
        end
        p:endDialog()
    end
end

RegisterNPCDef(npc)
