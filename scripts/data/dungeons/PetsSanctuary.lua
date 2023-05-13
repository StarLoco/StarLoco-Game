local group1 = {
    {1038, {1, 2, 3, 4, 5}},
    {813, {1, 2, 3, 4, 5}}
}

local group2 = {
    {2606, {1, 2, 3, 4, 5}},
    {1037, {1, 2, 3, 4, 5}},
    {631, {1, 2, 3, 4, 5}},
    {631, {1, 2, 3, 4, 5}}
}

local group3 = {
    {815, {1, 2, 3, 4, 5}},
    {813, {1, 2, 3, 4, 5}},
    {586, {1, 2, 3, 4, 5}},
    {2606, {1, 2, 3, 4, 5}}
}

local group4 = {
    {765, {1, 2, 3, 4, 5}},
    {765, {1, 2, 3, 4, 5}},
    {661, {1, 2, 3, 4, 5}},
    {661, {1, 2, 3, 4, 5}}
}

local group5 = {
    {632, {1, 2, 3, 4, 5}},
    {632, {1, 2, 3, 4, 5}},
    {663, {1, 2, 3, 4, 5}},
    {815, {1, 2, 3, 4, 5}}
}

local group6 = {
    {2606, {1, 2, 3, 4, 5}},
    {1038, {1, 2, 3, 4, 5}},
    {1037, {1, 2, 3, 4, 5}},
    {817, {1, 2, 3, 4, 5}},
    {817, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [8979] = {groupCell = 169, group = group1, winDest = {8980, 378}},
    [8980] = {groupCell = 137, group = group2, winDest = {8981, 341}},
    [8981] = {groupCell = 236, group = group3, winDest = {8982, 372}},
    [8982] = {groupCell = 184, group = group4, winDest = {8983, 296}},
    [8983] = {groupCell = 329, group = group5, winDest = {8984, 438}},
    [8984] = {groupCell = 183, group = group6, winDest = {9716, 438}}
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
