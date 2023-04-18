local group1 = {
    {970, {1,2,3,4,5}},
    {974, {1,2,3,4,5}},
    {975, {1,2,3,4,5}},
    {978, {1,2,3,4,5}},
    {982, {1,2,3,4,5}}
}
local group2 = {
    {970, {1,2,3,4,5}},
    {971, {1,2,3,4,5}},
    {979, {1,2,3,4,5}},
    {980, {1,2,3,4,5}},
    {981, {1,2,3,4,5}},
    {998, {1,2,3,4,5}}
}

local group3 = {
    {972, {1,2,3,4,5}},
    {973, {1,2,3,4,5}},
    {976, {1,2,3,4,5}},
    {977, {1,2,3,4,5}},
    {983, {1,2,3,4,5}},
    {984, {1,2,3,4,5}},
    {996, {1,2,3,4,5}}
}

local group4 = {
    {971, {1,2,3,4,5}},
    {976, {1,2,3,4,5}},
    {978, {1,2,3,4,5}},
    {980, {1,2,3,4,5}},
    {981, {1,2,3,4,5}},
    {983, {1,2,3,4,5}},
    {996, {1,2,3,4,5}},
    {1001, {1,2,3,4,5}}
}

local mapInfos = {
    [10360] = {groupCell= 238, group= group1, winDest= {10361,379}},
    [10361] = {groupCell= 256, group= group2, winDest= {10362,320}},
    [10362] = {groupCell= 373, group= group3, winDest= {10363,305}},
    [10363] = {groupCell= 213, group= group4, winDest= {10364,305}}
}

for k,v in pairs(mapInfos) do
    local map = MAPS[k]
    if map then
        map.onFightEnd[PVMFightType] = fightEndTeleportWinnerPlayers(v.winDest[1], v.winDest[2])
        map.staticGroups = {
            {v.groupCell, v.group}
        }
    end
end
