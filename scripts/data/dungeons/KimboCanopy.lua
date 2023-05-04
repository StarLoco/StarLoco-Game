local group1 = {
    {1020, {1,2,3,4,5}}
}

local group2 = {
    {1043, {1,2,3,4,5}},
    {1044, {1,2,3,4,5}}
}

local group3 = {
    {1020, {1,2,3,4,5}},
    {1043, {1,2,3,4,5}},
    {1047, {1,2,3,4,5}}
}

local group4 = {
    {1044, {1,2,3,4,5}},
    {1044, {1,2,3,4,5}},
    {1046, {1,2,3,4,5}},
    {1046, {1,2,3,4,5}}
}

local group5 = {
    {1043, {1,2,3,4,5}},
    {1043, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1046, {1,2,3,4,5}},
    {1047, {1,2,3,4,5}}
}

local group6 = {
    {1020, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}}
}

local group7 = {
    {1046, {1,2,3,4,5}},
    {1046, {1,2,3,4,5}},
    {1047, {1,2,3,4,5}},
    {1047, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1043, {1,2,3,4,5}},
    {1044, {1,2,3,4,5}}
}

local group8 = {
    {1045, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1020, {1,2,3,4,5}},
    {1044, {1,2,3,4,5}},
    {1044, {1,2,3,4,5}},
    {1043, {1,2,3,4,5}},
    {1046, {1,2,3,4,5}},
    {1047, {1,2,3,4,5}}
}

local mapInfos = {
    [11108] = {groupCell= 181, group= group1, winDest= {11111,173}},
    [11111] = {groupCell= 308, group= group2, winDest= {11113,332}},
    [11113] = {groupCell= 255, group= group3, winDest= {11115,306}},
    [11115] = {groupCell= 184, group= group4, winDest= {11121,376}},
    [11121] = {groupCell= 252, group= group5, winDest= {11127,421}},
    [11127] = {groupCell= 313, group= group6, winDest= {11128,421}},
    [11128] = {groupCell= 282, group= group7, winDest= {11133,247}},
    [11133] = {groupCell= 283, group= group8, winDest= {11518,216}}
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
