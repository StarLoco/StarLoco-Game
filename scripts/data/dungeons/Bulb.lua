local group1 = {
    {515, {1,2,3,4,5}},
    {518, {1,2,3,4,5}},
    {548, {1,2,3,4,5}},
    {520, {1,2,3,4,5}},
    {112, {1,2,3,4,5}}
}
local group2 = {
    {515, {1,2,3,4,5}},
    {515, {1,2,3,4,5}},
    {518, {1,2,3,4,5}},
    {518, {1,2,3,4,5}},
    {548, {1,2,3,4,5}},
    {548, {1,2,3,4,5}},
    {520, {1,2,3,4,5}},
    {520, {1,2,3,4,5}}
}

local group3 = {
    {520, {1,2,3,4,5}},
    {520, {1,2,3,4,5}},
    {520, {1,2,3,4,5}},
    {515, {1,2,3,4,5}},
    {518, {1,2,3,4,5}},
    {548, {1,2,3,4,5}}
}

local group4 = {
    {522, {1,2,3,4,5}},
    {520, {1,2,3,4,5}},
    {515, {1,2,3,4,5}},
    {518, {1,2,3,4,5}},
    {548, {1,2,3,4,5}}
}

local group5 = {
    {518, {1,2,3,4,5}},
    {518, {1,2,3,4,5}},
    {522, {1,2,3,4,5}},
    {515, {1,2,3,4,5}},
    {548, {1,2,3,4,5}}
}

local group6 = {
    {519, {1,2,3,4,5}},
    {515, {1,2,3,4,5}},
    {518, {1,2,3,4,5}},
    {548, {1,2,3,4,5}}
}

local mapInfos = {
    [8291] = {groupCell= 98, group= group1, winDest= {8295,422}},
    [8295] = {groupCell= 148, group= group2, winDest= {8290,102}},
    [8290] = {groupCell= 297, group= group3, winDest= {8293,40}},
    [8293] = {groupCell= 125, group= group4, winDest= {8286,422}},
    [8294] = {groupCell= 331, group= group5, winDest= {8296,392}},
    [8296] = {groupCell= 153, group= group6, winDest= {8351,392}}
}

for k,v in pairs(mapInfos) do
    local map = MAPS[k]
    if map then
        map.onFightEnd[PVMFightType] = fightEndTeleportWinnerPlayer(v.winDest[1], v.winDest[2])
        map.staticGroups = {
            {v.groupCell, v.group}
        }
    end
end
