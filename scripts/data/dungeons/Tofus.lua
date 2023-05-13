local group1 = {
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}}
}

local group2 = {
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}},
    {804, {1, 2, 3, 4, 5}},
    {804, {1, 2, 3, 4, 5}}
}

local grouptofuboss = {
    {800, {1, 2, 3, 4, 5}},
    {804, {1, 2, 3, 4, 5}},
    {806, {1, 2, 3, 4, 5}},
    {808, {1, 2, 3, 4, 5}},
    {796, {1, 2, 3, 4, 5}},
    {796, {1, 2, 3, 4, 5}},
    {796, {1, 2, 3, 4, 5}},
    {98, {1, 2, 3, 4, 5}}
}

local grouproyaltofu = {
    {382, {1, 2, 3, 4, 5}},
    {808, {1, 2, 3, 4, 5}},
    {808, {1, 2, 3, 4, 5}},
    {804, {1, 2, 3, 4, 5}},
    {804, {1, 2, 3, 4, 5}},
    {796, {1, 2, 3, 4, 5}},
    {796, {1, 2, 3, 4, 5}},
    {806, {1, 2, 3, 4, 5}}
}

local mapInfos = {

    [9521] = {groupCell = 228, group= group1, winDest = {9523, 436}},
    [9523] = {winDest = {9524, 436}},
    [9524] = {winDest = {9525, 436}},
    [9530] = {winDest = {9533, 450}},
    [9533] = {winDest = {9765, 436}},
    [9765] = {groupCell = 168, group= group2, winDest = {9766, 436}},
    [9766] = {groupCell = 311, group= grouptofuboss, winDest = {9522, 436}},
    [9528] = {winDest = {9531, 434}},
    [9531] = {winDest = {9534, 434}},
    [9529] = {winDest = {9532, 436}},
    [9532] = {winDest = {9534, 434}},
    [9534] = {winDest = {9535, 436}},
    [9535] = {groupCell = 340, group= grouproyaltofu, winDest = {9522, 436}}
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
