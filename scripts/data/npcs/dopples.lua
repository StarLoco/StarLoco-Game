--- Dopple master NPCs
--- Start dopple quests, fights and trade doploons
---
---
--- On Official server, each NPC has its own response IDs

local templeNpcInfo = {
    [FecaBreed] = {npc=433, init=1769},
    [SramBreed] = {npc=441, init=1773} -- Responses 6760;1575;1426
}

--- Trade 6697
--- Fight each 6772
--- Info 1419
--- Train 1509


function onTalkDoppleMaster()
    return function(p, answer)

    end
end