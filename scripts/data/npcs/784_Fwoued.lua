local npc = Npc(784, 50)

npc.colors = {3248109, 16724531, 16773874}
npc.accessories = {0, 8285, 8281, 0, 0}

local mazeKeyId = 7511
local dungeonKeyId = 8320
local enterMazeDest = {9396, 387}
local enterDungeonDest = {8541, 336}

---@param p Player
---@param answer number
local function dungeonInfoDialog(p, answer)
    if answer == 2850 then p:ask(3218, {2851})
    elseif answer == 2851 then p:ask(3219, {2857})
    elseif answer == 2857 then p:ask(3224, {2858})
    elseif answer == 2858 then p:ask(3225, {2859})
    elseif answer == 2859 then p:ask(3226, {2860})
    elseif answer == 2860 then p:ask(3227, {2861})
    elseif answer == 2861 then p:ask(3228)
    end
end

---@param p Player
---@param answer number
local function talkMapFinalRoom(p, answer)
    if answer == 0 then
        p:ask(3231, {2855})
    elseif answer == 2855 then
        p:teleport(9397,90)
        p:endDialog()
    end
end

---@param p Player
---@param answer number
local function talkMapDungeonEntrance(p, answer)
    -- Player needs the dungeon key AND either the maze keychain or key
    local hasDungeonKey = p:getItem(dungeonKeyId)
    local hasMazeKeyOrKeyChain = p:getItem(mazeKeyId) or hasKeyChainFor(p, mazeKeyId)

    if answer == 0 then
        local responses = (hasDungeonKey and hasMazeKeyOrKeyChain) and {2856} or {}
        p:ask(3223, responses)
    elseif answer == 2856 then
        if not hasDungeonKey or not hasMazeKeyOrKeyChain then
            -- Should not happen (cheat?)
            p:endDialog()
            return
        end

        if not useKeyChainFor(p, mazeKeyId) then
            if not p:consumeItem(mazeKeyId, 1) then
                -- Should not happen (cheat?)
                p:endDialog()
                return
            end
        end

        -- We consumed the maze key(chain), let's consume the dungeon key too
        if p:consumeItem(dungeonKeyId, 1) then
            p:teleport(enterDungeonDest[1], enterDungeonDest[2])
            p:endDialog()
            return
        end
        -- Should not happen (cheat?)
        p:endDialog()
    end
end

---@param p Player
---@param answer number
local function talkMapSlabsRoom(p, answer)
    if answer == 0 then
        p:ask(3221, {2854})
    elseif answer == 2854 then
        p:ask(3222, {2855})
    elseif answer == 2855 then
        p:teleport(9397, 90)
        p:endDialog()
    end
end

---@param p Player
---@param answer number
local function talkMapMazeEntrance(p, answer)
    local showKeyResponse = 2847

    if answer == 0 then
        local responses = {}

        if p:getItem(mazeKeyId) or hasKeyChainFor(p, mazeKeyId) then
            table.insert(responses, showKeyResponse)
        end
        table.insert(responses, 2849)
        p:ask(3216, responses)
    elseif answer == showKeyResponse then
        p:teleport(enterMazeDest[1], enterMazeDest[2])
        p:endDialog()
    elseif answer == 2849 then
        p:ask(3217, {2850})
    else
        dungeonInfoDialog(p, answer)
    end
end

---@param p Player
---@param answer number
local function talkInsideMaze(p, answer)
    JLogF("Called onTalk for inside maze map {}", p:mapID())
    if answer == 0 then
        p:ask(3220, {2853, 2852})
    elseif answer == 2853 then
        p:teleport(9396, 387)
        p:endDialog()
    elseif answer == 2852 then
        p:ask(3217, {2850})
    else
        dungeonInfoDialog(p, answer)
    end
end

local onTalkMaps = {
    [9989] = talkMapFinalRoom,
    [9395] = talkMapDungeonEntrance,
    [9396] = talkMapSlabsRoom,
    [9397] = talkMapMazeEntrance
}

for i = 9371, 9394 do
    onTalkMaps[i] = talkInsideMaze
end

function npc:onTalk(p, answer)
    JLogF("Called onTalk map {}", p:mapID())
    if onTalkMaps[p:mapID()] then
        onTalkMaps[p:mapID()](p, answer)
    end

end

RegisterNPCDef(npc)
