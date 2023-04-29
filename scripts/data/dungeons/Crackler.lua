local group1 = {
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}}
}

local group2 = {
    {826, {1,2,3,4,5}}
}

local group3 = {
    {669, {1,2,3,4,5}},
    {293, {1,2,3,4,5}},
    {293, {1,2,3,4,5}},
    {293, {1,2,3,4,5}},
    {293, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}},
    {483, {1,2,3,4,5}}
}

local mapInfos = {
    [9578] = {winDest= {9581,427}},
    [9581] = {winDest= {9582,235}},
    [9582] = {winDest= {9583,338}},
    [9583] = {winDest= {9584,36}},
    [9584] = {groupCell= 48, group= group1, winDest= {9585,165}},
    [9585] = {winDest= {9586,326}},
    [9586] = {winDest= {9587,360}},
    [9587] = {winDest= {9588,407}},
    [9864] = {groupCell= 167, group= group2, winDest= {9588,407}},
    [9580] = {groupCell= 168, group= group3, winDest= {749,291}},
}

for k,v in pairs(mapInfos) do
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
