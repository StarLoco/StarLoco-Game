local npc = Npc(797, 1001)

npc.gender = 1

local dungeon = TreechnidDungeon
local SoftOakKey = 8436

---@param p Player
---@param answer number
local function talkMapDungeonEntrance(p, answer)

    if answer == 0 then
        p:ask(3271, {2796})
    elseif answer == 2796 then
        -- Check if p has item / keychain
        local responses = {}
        if p:getItem(dungeon.keyID, 1) then table.insert(responses, dungeon.keyResponseID) end
        if dungeon:hasKeyChain(p) then table.insert(responses, dungeon.keychainResponseID) end
        p:ask(dungeon.questionID, responses)
    elseif answer == dungeon.keyResponseID then
        -- Use Key then teleport
        if p:consumeItem(dungeon.keyID, 1) then
            p:teleport(dungeon.tpDest[1], dungeon.tpDest[2])
        end
        p:endDialog()
    elseif answer == dungeon.keychainResponseID then
        -- Use Keychain then teleport
        if dungeon:useKeyChain(p) then
            p:teleport(dungeon.tpDest[1], dungeon.tpDest[2])
        end
        p:endDialog()
    end
end

local function talkMapTreechnidMorphRoom(p, answer)
    if answer == 0 then
        p:ask(3276, {2946})
    elseif answer == 2946 then
        --TODO: Morph
        p:teleport(8716, 184)
        p:endDialog()
    end
end

local function talkMapTreechnidUnmorphRoom(p, answer)
    if answer == 0 then
        p:ask(3280, {2933})
    elseif answer == 2933 then
        --TODO: Unmorph
        p:teleport(10154, 335)
        p:endDialog()
    end
end

local function talkMapSlabsRoom(p, answer)
    local cell = p:cell()
    local orientation = p:orientation()
    if not (cell == 142 and orientation == 7) then
        p:ask(3273)
        return
    end
    if answer == 0 then
        p:ask(3274, {2928})
    elseif answer == 2928 then
        p:teleport(8721, 96)
        p:endDialog()
    end
end

local function talkMapSoftOakEntrance(p, answer)
    local hasKey = p:getItem(SoftOakKey)
    local hasKeyChain = hasKeyChainFor(p, SoftOakKey)
    local hasKeyOrKeyChain = p:getItem(SoftOakKey) or hasKeyChainFor(p, SoftOakKey)
    local showKeyResponse = 2931
    local showKeyChainResponse = 6606
    local showBothKeysResponses = 2930

    if answer == 0 then
        local responses = hasKeyOrKeyChain and {showBothKeysResponses} or {}
        p:ask(3276, responses)
    elseif answer == showBothKeysResponses then
        local responses = {}
        if hasKey then
            table.insert(responses, showKeyResponse)
        end
        if hasKeyChain then
            table.insert(responses, showKeyChainResponse)
        end
        p:ask(3277, responses)
    elseif answer == showKeyResponse and hasKey then
        p:consumeItem(SoftOakKey, 1)
        p:teleport(8714, 112)
        p:endDialog()
    elseif answer == showKeyChainResponse and hasKeyChain then
        useKeyChainFor(p, SoftOakKey)
        p:teleport(8714, 112)
        p:endDialog()
    end
end

local function talkMapSoftOakMorphRoom(p, answer)
    if answer == 0 then
        p:ask(3276, {2946})
    elseif answer == 2946 then
        --TODO: Morph
        p:teleport(9121, 69)
        p:endDialog()
    end
end

local function talkMapSoftOakUnmorphRoom(p, answer)
    if answer == 0 then
        p:ask(3280, {2933})
    elseif answer == 2933 then
        --TODO: Unmorph
        p:teleport(9125, 452)
        p:endDialog()
    end
end

local function talkMapAncestralTreechnidExit(p, answer)
    if answer == 0 then
        p:ask(3275, {2929})
    elseif answer == 2929 then
        p:teleport(9127, 236)
        p:endDialog()
    end
end

local function talkMapSoftOakExit(p, answer)
    if answer == 0 then
        p:ask(3278, {2932})
    elseif answer == 2932 then
        p:teleport(9127, 236)
        p:endDialog()
    end
end

local onTalkMaps = {
    [9127] = talkMapDungeonEntrance,
    [8715] = talkMapTreechnidMorphRoom,
    [8719] = talkMapTreechnidUnmorphRoom,
    [10154] = talkMapSlabsRoom,
    [8721] = talkMapSoftOakEntrance,
    [9120] = talkMapSoftOakMorphRoom,
    [9123] = talkMapSoftOakUnmorphRoom,
    [10235] = talkMapAncestralTreechnidExit,
    [9126] = talkMapSoftOakExit
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if onTalkMaps[p:mapID()] then
        onTalkMaps[p:mapID()](p, answer)
    end
end

RegisterNPCDef(npc)
