local group1 = {
    {827, {1, 2, 3, 4, 5}},
    {121, {1, 2, 3, 4, 5}},
    {832, {1, 2, 3, 4, 5}},
    {831, {1, 2, 3, 4, 5}},
    {668, {1, 2, 3, 4, 5}},
    {668, {1, 2, 3, 4, 5}},
    {830, {1, 2, 3, 4, 5}},
    {830, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [9879] = {groupCell = 322, group = group1, winDest = {9881, 437}}
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
