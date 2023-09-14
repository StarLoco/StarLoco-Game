
--- Stores maze rooms with their Top/Right/Bottom/Left (clockwise) levers and doors. -1 when non existent
local roomInfo = {
    [9553] = {levers={-1, 245, 429, -1}, doors={-1, 288, 428, -1}},
    [9564] = {levers={-1, 230, 400, -1}, doors={-1, 259, 429, 335}},
    [9571] = {levers={-1, 302, 399, 248}, doors={-1, 331, 428, 277}},
    [9572] = {levers={-1, -1, 399,234}, doors={-1, 346, 443, 263}},
    [9573] = {levers={-1, -1, 396, 190}, doors={-1, -1, 440, 219}},

    [9574] = {levers={-1, 244, 396, -1}, doors={51, 273, 425, -1}},
    [9575] = {levers={-1, 330, 385, -1}, doors={94, 331, 427, 335}},
    [9576] = {levers={-1, 288, 412, 277}, doors={67, 317, 441, 306}},
    [9577] = {levers={-1, 403, 427, 366}, doors={50, 390, 426, 364}},
    [9554] = {levers={-1, -1, 277,402}, doors={138, -1, 431, 306}},

    [9555] = {levers={78, 345, 396, -1}, doors={79, 332, 440, -1}},
    [9556] = {levers={93, 260, 444, -1}, doors={64, 332, 428, 306}},
    [9557] = {levers={94, 201, 369, 262}, doors={51, 230, 413, 306}},
    [9558] = {levers={78, -1, 383, 278}, doors={77, 361, 427, 277}},
    [9559] = {levers={66, -1, 428, 190}, doors={65, -1, 427, 277}},

    [9560] = {levers={109, 301, -1, -1}, doors={52, 302, 428, -1}},
    [9561] = {levers={79, 288, -1, -1}, doors={80, 317, 441, 306}},
    [9562] = {levers={96, 260, -1, 276}, doors={52, 303, 429, 320}},
    [9563] = {levers={51, -1, -1, 293}, doors={52, 288, 429, 292}},
    [9565] = {levers={80, -1, -1, 233}, doors={51, -1, 414, 262}},

    [9566] = {levers={50, 274, -1, -1}, doors={51, 332, -1, -1}},
    [9567] = {levers={50, 260, -1, -1}, doors={37, 346, -1, 277}},
    [9568] = {levers={50, 260, -1, 190}, doors={51, 346, -1, 277}},
    [9569] = {levers={63, -1, -1, 247}, doors={64, 317, -1, 291}},
    [9570] = {levers={50, -1, -1, 247}, doors={51, -1, -1, 306}},
}

local roomPositions = {
    {9553, 9564, 9571, 9572, 9573},
    {9574, 9575, 9576, 9577, 9554},
    {9555, 9556, 9557, 9558, 9559},
    {9560, 9561, 9562, 9563, 9565},
    {9566, 9567, 9568, 9569, 9570}
}

-- Update roomInfo with positions
for y, row in ipairs(roomPositions) do
    for x, mapID in ipairs(row) do
        local top = (roomPositions[y-1] and roomPositions[y-1][x]) or -1
        local right = (roomPositions[y] and roomPositions[y][x+1]) or -1
        local bottom = (roomPositions[y+1] and roomPositions[y+1][x]) or -1
        local left = (roomPositions[y] and roomPositions[y][x-1]) or -1
        roomInfo[mapID].neighbors = {top, right, bottom, left}
    end
end


--region Lever activation
---@param mapID number
---@param dir number (1/2/3/4) for lever position
---@return boolean (true if worked, false if not)
local dofus1LeverBehavior = function(mapID, dir)
    -- Bottom lever opens bottom door of bottom map
    local dstMapID = roomInfo[mapID].neighbors[dir]
    if dstMapID == -1 then
        error("MinosMaze: dstMapID is -1 for mapID %d and dir %d", mapID, dir)
        return
    end

    local dstMapID2 = roomInfo[dstMapID].neighbors[dir]

    local doorCellID = roomInfo[dstMapID].doors[dir]
    if doorCellID == -1 then
        error("MinosMaze: doorCellID is -1 for mapID %d and dir %d", dstMapID, dir)
        return
    end

    local dstMap = World:map(dstMapID)
    if dstMap:getAnimationState(doorCellID) ~= AnimStates.NOT_READY then return end
    dstMap:setAnimationState(doorCellID, AnimStates.READYING)

    local oppositeDir = 1 + ((dir + 1) % 4)
    if dstMapID2 ~= -1 then
        local dstMap2 = World:map(dstMapID2)
        local doorCellID2 = roomInfo[dstMapID2].doors[oppositeDir]

        if doorCellID2== -1 then
            error("MinosMaze: doorCellID is -1 for mapID %d and dir %d", dstMapID2, dir)
            return
        end


        if dstMap2:getAnimationState(doorCellID2) ~= AnimStates.NOT_READY then return end
        dstMap2:setAnimationState(doorCellID2, AnimStates.READYING)
    end

    World:delayForMs(10000, function()
        dstMap:setAnimationState(doorCellID, AnimStates.IN_USE)

        if dstMapID2 ~= -1 then
            local dstMap2 = World:map(dstMapID2)
            local doorCellID2 = roomInfo[dstMapID2].doors[oppositeDir]

            dstMap2:setAnimationState(doorCellID2, AnimStates.IN_USE)
        end
    end)

end

--endregion

---@param dir number Direction of the lever (1,2,3,4) for Top,Right,Bottom,Left
local onSwitchActivation = function(dir)
    ---@param p Player
    return function(p)
        JLogF("MinosMaze: {} activated lever {} on map #{}", p:name(), dir, p:mapID())
        dofus1LeverBehavior(p:mapID(), dir)
        return true
    end
end

--region Add door and switches to maps
local addDoor = function(map, cellId, anim)
    if cellId == -1 then return end
    map.animations[cellId] = anim
end

local addLever = function(map, cellId, dir)
    if cellId == -1 then return end

    local anim = AnimatedObjects.MinosMazeBlueSwitch
    if dir == 2 or dir == 4 then
        anim = AnimatedObjects.MinosMazeYellowSwitch
    end
    map.objects[cellId] = Objects.Switch
    map.animations[cellId] = anim
    map.switches[cellId] = onSwitchActivation(dir)
end

for k, v in pairs(roomInfo) do
    local map = MAPS[k]
    if map then
        -- Add doors
        addDoor(map, v.doors[1], AnimatedObjects.MinosMazeBlueDoor)
        addDoor(map, v.doors[2], AnimatedObjects.MinosMazeYellowDoor)
        addDoor(map, v.doors[3], AnimatedObjects.MinosMazeBlueDoor)
        addDoor(map, v.doors[4], AnimatedObjects.MinosMazeYellowDoor)

        -- Add levers
        addLever(map, v.levers[1], 1)
        addLever(map, v.levers[2], 2)
        addLever(map, v.levers[3], 3)
        addLever(map, v.levers[4], 4)
    else
        JLogF("MinosMaze: Map #{} not found", k)
    end
end
--endregion