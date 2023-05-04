local group1 = {
    {1058, {1,2,3,4,5}}
}

local group2 = {
    {1057, {1,2,3,4,5}},
    {1058, {1,2,3,4,5}},
    {1058, {1,2,3,4,5}},
    {1058, {1,2,3,4,5}},
    {1059, {1,2,3,4,5}},
    {1059, {1,2,3,4,5}},
    {1059, {1,2,3,4,5}}
}

local group3 = {
    {423, {1,2,3,4,5}}
}

local mapInfos = {
    [10693] = {groupCell= 295, group= group1, winDest= {11096,448}},
    [11096] = {groupCell= 355, group= group2, winDest= {11095,783}},
    [11095] = {groupCell= 509, group= group3, winDest= {10810,113}}
}

for k,v in pairs(mapInfos) do
    local map = MAPS[k]
    if map then
        map.onFightEnd[PVMFightType] = fightEndTeleportWinnerPlayer(v.winDest[1], v.winDest[2])
        map.staticGroups = {
            {v.groupCell, v.group}
        }
    end
end
