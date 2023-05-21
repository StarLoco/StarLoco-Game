local group1 = {
    {780, {1, 2, 3, 4, 5}},
    {673, {1, 2, 3, 4, 5}},
    {675, {1, 2, 3, 4, 5}},
    {677, {1, 2, 3, 4, 5}},
    {681, {1, 2, 3, 4, 5}},
    {676, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    [8969] = {winDest = {8968, 379}},
    [8968] = {winDest = {8967, 218}},
    [8967] = {winDest = {8966, 262}},
    [8977] = {groupCell = 227, group = group1, winDest = {8978, 450}}
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
