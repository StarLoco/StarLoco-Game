local group1 = {
    {797, {1,2,3,4,5}},
    {795, {1,2,3,4,5}},
    {795, {1,2,3,4,5}},
    {795, {1,2,3,4,5}},
    {194, {1,2,3,4,5}},
    {198, {1,2,3,4,5}},
    {240, {1,2,3,4,5}},
    {241, {1,2,3,4,5}}
}

local mapInfos = {
    [9775] = {winDest= {9776,422}},
    [9778] = {winDest= {9779,436}},
    [9779] = {winDest= {9780,436}},
    [9780] = {winDest= {9781,394}},
    [9781] = {winDest= {9782,436}},
    [9782] = {winDest= {9784,436}},
    [9784] = {groupCell= 178, group= group1, winDest= {9785,436}}
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
