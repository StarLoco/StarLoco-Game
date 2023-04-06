local npc = Npc(396, 9016)

npc.colors = {2654703, 4033774, 13762560}

function npc:onTalk(p, answer)
    -- TODO: Check ticket date + kamas ?
    -- TODO: Missing a way to create an item with a txt stat date
    if answer == 0 then p:ask(1686, {1302})
    elseif answer == 1302 then
        if p:modKamas() then
            p:addItem(1728, 1, false)
            p:teleport(1856, 226)
        end
        p:endDialog()
    end
end

RegisterNPCDef(npc)
