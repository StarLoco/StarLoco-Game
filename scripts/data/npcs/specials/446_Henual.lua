local npc = Npc(446, 50)
--TODO: Sniffer gfx+dialogs offi > Il est map 2.1 mais il faut y aller à 7 joueurs pour arriver jusqu'à lui.
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2660)
--TODO: if we speak to him in the five minutes before each hour, he will respond 2611
    end
end

RegisterNPCDef(npc)