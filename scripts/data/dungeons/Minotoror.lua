local group1 = {
    {121, {1, 2, 3, 4, 5}},
    {832, {1, 2, 3, 4, 5}},
    {831, {1, 2, 3, 4, 5}},
    {668, {1, 2, 3, 4, 5}},
    {668, {1, 2, 3, 4, 5}},
    {830, {1, 2, 3, 4, 5}},
    {830, {1, 2, 3, 4, 5}},
    {829, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [9880] = {groupCell = 284, group = group1, winDest = {9877, 394}}
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
