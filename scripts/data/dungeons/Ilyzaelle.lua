local group1 = {
    {1212, {1,2,3,4,5}},
    {1212, {1,2,3,4,5}},
    {1212, {1,2,3,4,5}},
    {1211, {1,2,3,4,5}},
	{1211, {1,2,3,4,5}},
	{1211, {1,2,3,4,5}},
    {1211, {1,2,3,4,5}}
}

local group2 = {
    {1214, {1,2,3,4,5}},
    {1212, {1,2,3,4,5}},
    {1212, {1,2,3,4,5}},
    {1212, {1,2,3,4,5}},
    {1212, {1,2,3,4,5}}
}

local group3 = {
    {1215, {1,2,3,4,5}},
    {1215, {1,2,3,4,5}},
    {1216, {1,2,3,4,5}},
    {1216, {1,2,3,4,5}},
    {1216, {1,2,3,4,5}},
    {1216, {1,2,3,4,5}}
}

local group4 = {
    {1213, {1,2,3,4,5}},
    {1213, {1,2,3,4,5}},
    {1213, {1,2,3,4,5}},
    {1216, {1,2,3,4,5}},
	{1216, {1,2,3,4,5}},
	{1216, {1,2,3,4,5}},
	{1216, {1,2,3,4,5}},
    {1216, {1,2,3,4,5}}
}

local group5 = {
    {1170, {1,2,3,4,5}},
    {1207, {1,2,3,4,5}},
    {1206, {1,2,3,4,5}},
    {1205, {1,2,3,4,5}},
    {1204, {1,2,3,4,5}}
}

local mapInfos = {
    [11971] = {groupCell= 108, group= group1, winDest= {11973,404}},
    [11973] = {groupCell= 167, group= group2, winDest= {11972,384}},
    [11972] = {groupCell= 253, group= group3, winDest= {11974,160}},
    [11974] = {groupCell= 183, group= group4, winDest= {11970,324}},
    [11976] = {groupCell= 123, group= group5, winDest= {4383,368}}
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
