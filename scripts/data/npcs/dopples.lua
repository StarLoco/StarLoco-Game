--- Dopple master NPCs
--- Start dopple quests, fights and trade doploons
---
---
--- On Official server, each NPC has its own response IDs

local templeNpcInfo = {
    [FecaBreed]     = {npc=433, init=1769},
    [IopBreed]      = {npc=434, init=1771},-- |1424;1559;6754
    [EniripsaBreed] = {npc=435, init=1768}, -- |6751;1535;6747
    [OsamodasBreed] = {npc=436, init=1766}, -- |6755;1418;1485
    [XelorBreed]    = {npc=437, init=1758}, -- |1415;168;6761
    [EcaflipBreed]  = {npc=438, init=1772}, -- |1515;6750;1425
    [CraBreed]      = {npc=439, init=1767}, -- |1509;1419;6697
    [EnutrofBreed]  = {npc=440, init=1764}, -- |6752;172;1417
    [SramBreed]     = {npc=441, init=1773}, -- |6760;1575;1426
    [SadidaBreed]   = {npc=442, init=1770}, -- |1422;6759;1581
    [SacrierBreed]  = {npc=443, init=1799}, -- |6758;1520;1519
    [PandawaBreed]  = {npc=672, init=2851}, -- |6598;6757;6429
}

--- Trade       6697
--- Fight each  6772
--- Info        1419
--- Train       1509


function onTalkDoppleMaster()
    return function(p, answer)

    end
end