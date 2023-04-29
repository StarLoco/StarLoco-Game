local group1 = {
    {876, {1,2,3,4,5}},
    {876, {1,2,3,4,5}},
    {876, {1,2,3,4,5}},
    {876, {1,2,3,4,5}},
    {876, {1,2,3,4,5}}
}

local group2 = {
    {74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}}
}

local group3 = {
    {74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{876, {1,2,3,4,5}},
	{876, {1,2,3,4,5}},
	{876, {1,2,3,4,5}},
	{876, {1,2,3,4,5}}
}

local group4 = {
    {53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}}
}

local group5 = {
    {792, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{53, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{74, {1,2,3,4,5}},
	{876, {1,2,3,4,5}},
	{876, {1,2,3,4,5}}
}

local mapInfos = {
    [9750] = {winDest= {9751,399}},
    [9751] = {groupCell= 164, group= group1, winDest= {9752,366}},
    [9752] = {groupCell= 342, group= group2, winDest= {9753,380}},
    [9755] = {groupCell= 353, group= group3, winDest= {9757,375}},
	[9757] = {winDest= {9758,366}},
    [9758] = {groupCell= 314, group= group4, winDest= {9759,366}},
    [9759] = {winDest= {9760,403}},
	[9760] = {groupCell= 381, group= group5, winDest= {9767,403}}
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
