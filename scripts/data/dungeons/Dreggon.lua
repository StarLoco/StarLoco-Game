local group1 = {
    {854, {1, 2, 3, 4, 5}},
    {862, {1, 2, 3, 4, 5}},
    {862, {1, 2, 3, 4, 5}},
    {855, {1, 2, 3, 4, 5}},
    {858, {1, 2, 3, 4, 5}},
    {878, {1, 2, 3, 4, 5}},
    {879, {1, 2, 3, 4, 5}},
    {905, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [10098] = {winDest = {10099, 450}},
    [10099] = {winDest = {10100, 450}},
    [10100] = {winDest = {10101, 451}},
    [10101] = {winDest = {10102, 450}},
    [10102] = {winDest = {10103, 450}},
    [10103] = {winDest = {10106, 450}},
    [10106] = {winDest = {10107, 450}},
    [10107] = {winDest = {10109, 450}},
    [10109] = {groupCell = 237, group = group1, winDest = {10110, 403}}
}

for k, v in pairs(mapInfos) do
    local map = MAPS[k]
    if map then
        map.onFightEnd[PVMFightType] = fightEndTeleportWinnerPlayer(v.winDest[1], v.winDest[2])
        if v.group then
            map.staticGroups = {
                {v.groupCell, v.group}
            }
        end
    end
end
