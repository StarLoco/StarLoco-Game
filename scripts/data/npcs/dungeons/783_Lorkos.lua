local npc = Npc(783, 30)

npc.accessories = {0, 8284, 0, 0, 0}

local mazeKeyId = 7924
local dungeonKeyId = 8307
local enterMazeDest = {9876, 437}
local enterDungeonDest = {9879, 437}
local exitMazeOrDungeon = {9538, 186}

---@param p Player
---@param answer number
local function talkMapMazeEntrance(p, answer)
    local hasDungeonKey = p:getItem(mazeKeyId) or hasKeyChainFor(p, mazeKeyId)
    local showKeyResponse = 2844

    if answer == 0 then
        local responses = hasDungeonKey and {showKeyResponse, 2845} or {2845}
        p:ask(3204, responses)
    elseif answer == 2845 then
        p:ask(3214)
    elseif answer == showKeyResponse then
        if hasDungeonKey then
            p:teleport(enterMazeDest[1], enterMazeDest[2])
            p:endDialog()
        else
            p:endDialog()
        end
    end
end

---@param p Player
---@param answer number
local function talkMapSlabsRoom(p, answer)
    if answer == 0 then
        p:ask(3201, {2830, 2834})
    elseif answer == 2830 then
        p:ask(3202, {2831})
    elseif answer == 2831 then
        p:ask(3203, {2846})
    elseif answer == 2846 then
        p:ask(3215, {7656, 7655})
    elseif answer == 7655 then
        p:endDialog()
    elseif answer == 7656 then
        if p:getItem(8311) then
            p:consumeItem(8311, 1)
            p:teleport(9558, 266)
            p:endDialog()
        else
            p:ask(6789)
        end
    elseif answer == 2834 then
        p:ask(3206, {2835})
    elseif answer == 2835 then
        p:teleport(exitMazeOrDungeon[1], exitMazeOrDungeon[2])
        p:endDialog()
    end
end

---@param p Player
---@param answer number
local function talkInsideMaze(p, answer)
    if answer == 0 then
        p:ask(3207, {2836})
    elseif answer == 2836 then
        p:teleport(9876, 287)
        p:endDialog()
    end
end

---@param p Player
---@param answer number
local function talkMapDungeonEntrance(p, answer)
    local hasRelics = p:getItem(8305, 1) and p:getItem(8306, 1)
    local hasRelicsAndMazeKeyOrKeyChain = (p:getItem(mazeKeyId) or hasKeyChainFor(p, mazeKeyId)) and hasRelics
    local showBothKeysResponse = 2837
    local showKeyResponse = 2838
    local showKeyChainResponse = 6610
    if answer == 0 then

        local responses = (hasRelicsAndMazeKeyOrKeyChain) and {showBothKeysResponse, 2842, 2834} or {2842, 2834}
        p:ask(3208, responses)
    elseif answer == 2842 then
        p:ask(3212)
    elseif answer == 2834 then
        p:ask(3206, {2835})
    elseif answer == 2835 then
        p:teleport(exitMazeOrDungeon[1], exitMazeOrDungeon[2])
        p:endDialog()
    elseif answer == showBothKeysResponse then
        if not hasRelicsAndMazeKeyOrKeyChain then
            -- Should not happen (cheat?)
            p:endDialog()
            return
        end
        local hasMazeKeyRelics = p:getItem(mazeKeyId) and hasRelics
        local hazeMazeKeyChainRelics = hasKeyChainFor(p, mazeKeyId) and hasRelics

        local responses = {}
        if hasMazeKeyRelics then
            table.insert(responses, showKeyResponse) end
        if hazeMazeKeyChainRelics then
            table.insert(responses, showKeyChainResponse) end
        p:ask(3209, responses)
    elseif answer == showKeyResponse then
        local consumeRelicsKey = p:consumeItem(8305, 1) and p:consumeItem(8306, 1) and p:consumeItem(mazeKeyId, 1)
        if consumeRelicsKey then
            p:teleport(9880, 394)
            p:endDialog()
        else
            p:endDialog()
        end
    elseif answer == showKeyChainResponse then
        local consumeRelicsKeyChain = p:consumeItem(8305, 1) and p:consumeItem(8306, 1) and useKeyChainFor(p, mazeKeyId)
        if consumeRelicsKeyChain then
            p:teleport(9880, 394)
            p:endDialog()
        else
            p:endDialog()
        end
    end
end

---@param p Player
---@param answer number
local function talkMapMinotororExit(p, answer)
    local hasKey = p:getItem(dungeonKeyId)
    local hasKeyChain = hasKeyChainFor(p, dungeonKeyId)

    local showKeyResponse = 2841
    local showKeyChainResponse = 6625

    if answer == 0 then
        local responses = {}
        if hasKey and hasKeyChain then
            responses = {showKeyResponse, showKeyChainResponse, 2840, 2839}
        elseif hasKey then
            responses = {showKeyResponse, 2840, 2839}
        elseif hasKeyChain then
            responses = {showKeyChainResponse, 2840, 2839}
        else
            responses = {2840, 2839}
        end
        p:ask(3210, responses)
    elseif answer == 2840 then
        p:ask(3211)
    elseif answer == 2839 then
        p:teleport(exitMazeOrDungeon[1], exitMazeOrDungeon[2])
        p:endDialog()
    elseif answer == showKeyResponse and hasKey then
        if p:consumeItem(dungeonKeyId, 1) then
            p:teleport(enterDungeonDest[1], enterDungeonDest[2])
            p:endDialog()
        end
    elseif answer == showKeyChainResponse and hasKeyChain then
        if useKeyChainFor(p, dungeonKeyId) then
            p:teleport(enterDungeonDest[1], enterDungeonDest[2])
            p:endDialog()
        end
        p:endDialog()
    end
end

---@param p Player
---@param answer number
local function talkMapMinototExit(p, answer)
    if answer == 0 then
        p:ask(3219, {2843})
    elseif answer == 2843 then
        p:ask(3206, {2835})
    elseif answer == 2835 then
        p:teleport(exitMazeOrDungeon[1], exitMazeOrDungeon[2])
        p:endDialog()
    end
end

local onTalkMaps = {
    [9538] = talkMapMazeEntrance,
    [9876] = talkMapSlabsRoom,
    [9877] = talkMapMinotororExit,
    [9881] = talkMapMinototExit
}

for i = 9553, 9577 do
    onTalkMaps[i] = talkInsideMaze
end

-- Override maze central room
onTalkMaps[9557] = talkMapDungeonEntrance

for i = 9553, 9577 do
    if i ~= 9557 then
        onTalkMaps[i] = talkInsideMaze
    end
end

function npc:onTalk(p, answer)
    if onTalkMaps[p:mapID()] then
        onTalkMaps[p:mapID()](p, answer)
    end
end

RegisterNPCDef(npc)
