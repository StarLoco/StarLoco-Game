local npc = Npc(464, 1207)

function npc:onTalk(player, answer)
    if answer == 0 then player:ask(1930, {1662}, "[name]")
    elseif answer == 1662 then
        -- TODO: Buy beer -- OAKOd541bd5~1ac9~1~~6e#2###0d0+2;
        -- Lose 5 Kamas -- As packet + Im046;5
        player:ask(1931)
    end
end

RegisterNPCDef(npc)