local group1 = {
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}}
}

local group2 = {
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}}
}

local group3 = {
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}}
}

local group4 = {
    {601, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}}
}

local group5 = {
    {601, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}}
}

local group6 = {
    {601, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}}
}

local group7 = {
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}}
}

local group8 = {
    {600, {1, 2, 3, 4, 5}},
    {600, {1, 2, 3, 4, 5}},
    {600, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}}
}

local group9 = {
    {600, {1, 2, 3, 4, 5}},
    {600, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}}
}

local group10 = {
    {113, {1, 2, 3, 4, 5}},
    {600, {1, 2, 3, 4, 5}},
    {600, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {601, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {603, {1, 2, 3, 4, 5}},
    {123, {1, 2, 3, 4, 5}}
}

local mapInfos = {
    {mapId = 8541, groupCell = 223, group = group1, winDest = {8542, 198}},
    {mapId = 8542, groupCell = 316, group = group2, winDest = {8545, 320}},
    {mapId = 8545, groupCell = 373, group = group3, winDest = {8543, 403}},
    {mapId = 8543, groupCell = 199, group = group4, winDest = {8544, 345}},
    {mapId = 8544, groupCell = 169, group = group5, winDest = {8546, 245}},
    {mapId = 8546, groupCell = 370, group = group6, winDest = {8545, 74}},
    {mapId = 8545, groupCell = 150, group = group7, winDest = {8547, 393}},
    {mapId = 8547, groupCell = 312, group = group8, winDest = {8548, 407}},
    {mapId = 8548, groupCell = 295, group = group9, winDest = {8549, 422}},
    {mapId = 8549, groupCell = 283, group = group10, winDest = {9989, 422}}
}

---@param p Player
---@param isWinner boolean
---@param winners Fighter[]
---@param losers Fighter[]
local function endFight8545(p, isWinner, winners, losers)
    -- Count how many mob 123 were killed
    local count = countFightersForMobId(losers,123)

    local dest
    if count == 2 then
        -- Room 3
        dest = mapInfos[3].winDest
    else
        -- Room 7
        dest = mapInfos[7].winDest
    end

    p:teleport(dest[1], dest[2])
end

for _, v in ipairs(mapInfos) do
    local map = MAPS[v.mapId]
    if map then
        local endFight = fightEndTeleportWinnerPlayer(v.winDest[1], v.winDest[2])

        if v.mapId == 8545 then
            endFight = endFight8545
        end

        map.onFightEnd[PVMFightType] = endFight
        table.insert(map.staticGroups, {v.groupCell, v.group})
    end
end
