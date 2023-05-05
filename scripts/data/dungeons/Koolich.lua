local group1 = {
    {1070, {1, 2, 3, 4, 5}},
    {1026, {1, 2, 3, 4, 5}},
    {1019, {1, 2, 3, 4, 5}}
}

local group2 = {
    {1068, {1, 2, 3, 4, 5}},
    {1069, {1, 2, 3, 4, 5}},
    {1069, {1, 2, 3, 4, 5}},
    {1070, {1, 2, 3, 4, 5}}
}

local group3 = {
    {1019, {1, 2, 3, 4, 5}},
    {1019, {1, 2, 3, 4, 5}},
    {1019, {1, 2, 3, 4, 5}},
    {1019, {1, 2, 3, 4, 5}},
    {1019, {1, 2, 3, 4, 5}}
}

local group4 = {
    {1019, {1, 2, 3, 4, 5}},
    {1025, {1, 2, 3, 4, 5}},
    {1025, {1, 2, 3, 4, 5}},
    {1025, {1, 2, 3, 4, 5}},
    {1025, {1, 2, 3, 4, 5}},
    {1070, {1, 2, 3, 4, 5}}
}

local group5 = {
    {1068, {1, 2, 3, 4, 5}},
    {1068, {1, 2, 3, 4, 5}},
    {1069, {1, 2, 3, 4, 5}},
    {1069, {1, 2, 3, 4, 5}},
    {1070, {1, 2, 3, 4, 5}},
    {1070, {1, 2, 3, 4, 5}},
    {1070, {1, 2, 3, 4, 5}}
}

local group6 = {
    {1025, {1, 2, 3, 4, 5}},
    {1068, {1, 2, 3, 4, 5}},
    {1019, {1, 2, 3, 4, 5}},
    {1019, {1, 2, 3, 4, 5}},
    {1069, {1, 2, 3, 4, 5}},
    {1069, {1, 2, 3, 4, 5}},
    {1070, {1, 2, 3, 4, 5}},
    {1070, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [8950] = {groupCell = 324, group = group1, winDest = {8952, 394}},
    [8952] = {groupCell = 150, group = group2, winDest = {8953, 379}},
    [8953] = {groupCell = 342, group = group3, winDest = {8954, 378}},
    [8963] = {groupCell = 369, group = group4, winDest = {8964, 393}},
    [8964] = {groupCell = 296, group = group5, winDest = {8965, 394}},
    [8965] = {groupCell = 225, group = group6, winDest = {9717, 418}}
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
