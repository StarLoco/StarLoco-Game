local group1 = {
    {260, {1, 2, 3, 4, 5}},
    {651, {1, 2, 3, 4, 5}},
    {651, {1, 2, 3, 4, 5}},
    {651, {1, 2, 3, 4, 5}},
    {253, {1, 2, 3, 4, 5}},
    {253, {1, 2, 3, 4, 5}},
    {253, {1, 2, 3, 4, 5}},
    {253, {1, 2, 3, 4, 5}}
}

local group2 = {
    {992, {1, 2, 3, 4, 5}}
}

local group3 = {
    {992, {1, 2, 3, 4, 5}}
}

local group4 = {
    {258, {1, 2, 3, 4, 5}},
    {258, {1, 2, 3, 4, 5}},
    {260, {1, 2, 3, 4, 5}},
    {651, {1, 2, 3, 4, 5}},
    {651, {1, 2, 3, 4, 5}},
    {253, {1, 2, 3, 4, 5}},
    {253, {1, 2, 3, 4, 5}},
    {253, {1, 2, 3, 4, 5}}
}

local group5 = {
    {257, {1, 2, 3, 4, 5}},
    {258, {1, 2, 3, 4, 5}},
    {260, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [8714] = {groupCell = 253, group = group1, winDest = {9120, 112}},
    [9121] = {groupCell = 110, group = group2, winDest = {9122, 99}},
    [9122] = {groupCell = 183, group = group3, winDest = {9123, 113}},
    [9125] = {groupCell = 224, group = group4, winDest = {10153, 423}},
    [10153] = {groupCell = 109, group = group5, winDest = {9126, 423}}
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
