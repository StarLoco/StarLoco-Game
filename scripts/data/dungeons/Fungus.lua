local group1 = {
    {1153, {1, 2, 3, 4, 5}},
    {1154, {1, 2, 3, 4, 5}},
    {1155, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}},
    {1157, {1, 2, 3, 4, 5}}
}

local group2 = {
    {1155, {1, 2, 3, 4, 5}},
    {1155, {1, 2, 3, 4, 5}},
    {1157, {1, 2, 3, 4, 5}},
    {1157, {1, 2, 3, 4, 5}},
    {1158, {1, 2, 3, 4, 5}},
    {1158, {1, 2, 3, 4, 5}}
}

local group3 = {
    {1153, {1, 2, 3, 4, 5}},
    {1153, {1, 2, 3, 4, 5}},
    {1153, {1, 2, 3, 4, 5}},
    {1154, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}}
}

local group4left = {
    {1156, {1, 2, 3, 4, 5}},
    {1157, {1, 2, 3, 4, 5}}
}

local group4right = {
    {1153, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}}
}

local group5left = {
    {1155, {1, 2, 3, 4, 5}},
    {1157, {1, 2, 3, 4, 5}},
    {1158, {1, 2, 3, 4, 5}}
}

local group5right = {
    {1153, {1, 2, 3, 4, 5}},
    {1154, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}}
}

local group6 = {
    {1153, {1, 2, 3, 4, 5}},
    {1154, {1, 2, 3, 4, 5}},
    {1154, {1, 2, 3, 4, 5}},
    {1155, {1, 2, 3, 4, 5}},
    {1155, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}},
    {1157, {1, 2, 3, 4, 5}},
    {1158, {1, 2, 3, 4, 5}}
}

local group7 = {
    {1159, {1, 2, 3, 4, 5}},
    {1153, {1, 2, 3, 4, 5}},
    {1154, {1, 2, 3, 4, 5}},
    {1155, {1, 2, 3, 4, 5}},
    {1156, {1, 2, 3, 4, 5}},
    {1157, {1, 2, 3, 4, 5}},
    {1158, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [11931] = {groupCell = 179, group = group1, winDest = {11932, 378}},
    [11932] = {groupCell = 223, group = group2, winDest = {11933, 306}},
    [11933] = {groupCell = 253, group = group3, winDest = {11934, 422}},
    [11936] = {groupCell = 255, group = group4left, winDest = {11938, 457}},
    [11937] = {groupCell = 121, group = group4right, winDest = {11939, 276}},
    [11938] = {groupCell = 286, group = group5left, winDest = {11940, 431}},
    [11939] = {groupCell = 150, group = group5right, winDest = {11940, 431}},
    [11940] = {groupCell = 255, group = group6, winDest = {11941, 276}},
    [11941] = {groupCell = 356, group = group7, winDest = {9646, 408}}
}

for k, v in pairs(mapInfos) do
    local map = MAPS[k]
    if map then
        map.onFightEnd[PVMFightType] = fightEndTeleportWinnerPlayer(v.winDest[1], v.winDest[2])
        map.staticGroups = {
            {v.groupCell, v.group}
        }
    end
end
