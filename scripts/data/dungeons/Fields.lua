-- TODO: Double check grades

local group1 = {
    {59, {1,2,3}},
    {61, {1,2,3}},
    {79, {1,2,3}}
}

local group2 = {
    {61, {1,2,3}},
    {61, {1,2,3}},
    {31, {2,3,4}},
    {59, {1,2,3}}
}

local group3 = {
    {31, {1,2,3}},
    {31, {3,2,5}},
    {34, {4,5}},
    {59, {3,4}},
    {59, {4,5}}
}

local group4 = {
    {46, {1,2}},
    {46, {1,2}},
    {48, {2}},
    {48, {2}}
}

local group5 = {
    {59, {4,5}},
    {59, {4,5}},
    {59, {4,5}},
    {48, {2}},
    {48, {2,3}},
    {78, {4,5}}
}

local group6 = {
    {79, {4,5}},
    {79, {4,5}},
    {61, {1,2,3,4,5}},
    {61, {1,2,3,4,5}},
    {78, {2,3,4,5}},
    {78, {2,3,4,5}}
}

local group7 = {
    {799, {1,2,3,4,5}},
    {78, {1,2,3,4,5}},
    {48, {1,2,3,4,5}},
    {79, {1,2,3,4,5}},
    {59, {1,2,3,4,5}},
    {31, {1,2,3,4,5}},
    {34, {1,2,3,4,5}},
    {46, {1,2,3,4,5}}
}

local mapInfos = {
    [9768] = {groupCell= 199, group= group1, winDest= {9769,415}},
    [9769] = {groupCell= 266, group= group2, winDest= {9770,308}},
    [9770] = {groupCell= 241, group= group3, winDest= {9771,293}},
    [9771] = {groupCell= 270, group= group4, winDest= {9772,394}},
    [9772] = {groupCell= 179, group= group5, winDest= {9773,289}},
    [9773] = {groupCell= 240, group= group6, winDest= {9774,276}},
    [9774] = {groupCell= 215, group= group7, winDest= {9786,276}}
}

for k,v in pairs(mapInfos) do
    ---@type MapDef
    local map = MAPS[k]
    if map then
        map.onFightEnd[PVMFightType] = fightEndTeleportWinnerPlayer(v.winDest[1], v.winDest[2])
        map.staticGroups = {
            {v.groupCell, v.group}
        }
    end
end
