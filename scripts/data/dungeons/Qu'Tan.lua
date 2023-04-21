local group1 = {
    {1209, {1,2,3,4,5}},
    {1209, {1,2,3,4,5}},
    {1209, {1,2,3,4,5}},
    {1209, {1,2,3,4,5}},
	{1210, {1,2,3,4,5}},
	{1210, {1,2,3,4,5}},
    {1210, {1,2,3,4,5}}
}

local group2 = {
    {1217, {1,2,3,4,5}},
    {1210, {1,2,3,4,5}},
    {1210, {1,2,3,4,5}},
    {1210, {1,2,3,4,5}},
    {1210, {1,2,3,4,5}}
}

local group3 = {
    {1219, {1,2,3,4,5}},
    {1219, {1,2,3,4,5}},
    {1218, {1,2,3,4,5}},
    {1218, {1,2,3,4,5}},
    {1218, {1,2,3,4,5}},
    {1218, {1,2,3,4,5}}
}

local group4 = {
    {1220, {1,2,3,4,5}},
    {1220, {1,2,3,4,5}},
    {1220, {1,2,3,4,5}},
    {1218, {1,2,3,4,5}},
	{1218, {1,2,3,4,5}},
	{1218, {1,2,3,4,5}},
	{1218, {1,2,3,4,5}},
    {1218, {1,2,3,4,5}}
}

local group5 = {
    {1195, {1,2,3,4,5}},
    {1197, {1,2,3,4,5}},
    {1197, {1,2,3,4,5}},
    {1210, {1,2,3,4,5}},
    {1210, {1,2,3,4,5}}
}

local mapInfos = {
    [11965] = {groupCell= 324, group= group1, winDest= {11966,381}},
    [11966] = {groupCell= 299, group= group2, winDest= {11967,381}},
    [11967] = {groupCell= 343, group= group3, winDest= {11968,450}},
    [11968] = {groupCell= 382, group= group4, winDest= {11969,366}},
    [11975] = {groupCell= 167, group= group5, winDest= {4919,193}}
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
