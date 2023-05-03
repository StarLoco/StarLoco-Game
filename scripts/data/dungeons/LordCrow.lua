--- Lord Crow dungeon

--region Constants
local npcId = 774
local bossMobId = 289
local downMobId = 819
local rightMobId = 820

-- TODO: Fix npcPos and down/right cell destinations
local mapInfos = {
    -- -19/-62 (Entrance)
    [9590] = {groupsCells={166,169}, npcPos={205,1}, down={9594,44}, right={9591,117}, dialog=3185},
    -- -18/-62
    [9591] = {groupsCells={223,220}, npcPos={205,1}, down={9596,17}, right={9592,379}, dialog=3185},
    -- -17/-62
    [9592] = {groupsCells={193,196}, npcPos={205,1}, down={9597,378}, right={9593,453}, dialog=3187},
    -- -16/-62
    [9593] = {groupsCells={166,163}, npcPos={205,1}, down={9598,408}, right={9590,408}, dialog=3187},

    -- -19/-61
    [9594] = {groupsCells={356,359}, npcPos={205,1}, down={9599,173}, right={9596,17}, dialog=3185},
    -- -18/-61
    [9596] = {groupsCells={310,313}, npcPos={205,1}, down={9600,44}, right={9597,378}, dialog=3185},
    -- -17/-61
    [9597] = {groupsCells={166,357}, npcPos={205,1}, down={9601,18}, right={9598,408}, dialog=3187},
    -- -16/-61
    [9598] = {groupsCells={253,226}, npcPos={139,1}, down={9602,305}, right={9594,44}, dialog=3187},

    -- -19/-60
    [9599] = {groupsCells={313,310}, npcPos={205,1}, down={9603,419}, right={9600,44}, dialog=3193},
    -- -18/-60
    [9600] = {groupsCells={278,329}, npcPos={205,1}, down={9595,32}, right={9601,18}, dialog=3193},
    -- -17/-60
    [9601] = {groupsCells={193,196}, npcPos={205,1}, down={9723,392}, right={9602,305}, dialog=3194},
    -- -16/-60
    [9602] = {groupsCells={269,266}, npcPos={205,1}, down={9724,363}, right={9599,173}, dialog=3194},

    -- Cache says -18/-59 but let's use it as -19/-59
    [9603] = {groupsCells={233,236}, npcPos={205,1}, down={9590,408}, right={9595,32}, dialog=3193},
    -- -18/-59
    [9595] = {groupsCells={266,263}, npcPos={205,1}, down={9591,117}, right={9723,392}, dialog=3193},
    -- -17/-59
    [9723] = {groupsCells={250,253}, npcPos={205,1}, down={9592,379}, right={9724,363}, dialog=3194},
    -- -16/-59
    [9724] = {groupsCells={250,253}, npcPos={205,1}, down={9593,453}, right={9603,419}, dialog=3194}
}

---@type number[]
local mapIds = {}
for mapId in pairs(mapInfos) do
    table.insert(mapIds, mapId)
end

local bossGroup = {
    {289, {1,2,3,4,5}},
    {823, {1,2,3,4,5}},
    {824, {1,2,3,4,5}},
    {825, {1,2,3,4,5}}
}

local mobGroup1 = {
    {281, {1,2,3,4,5}},
    {281, {1,2,3,4,5}},
    {281, {1,2,3,4,5}},
    {818, {1,2,3,4,5}},
    {818, {1,2,3,4,5}},
    {820, {1,2,3,4,5}},
    {820, {1,2,3,4,5}},
}

local mobGroup2 = {
    {281, {1,2,3,4,5}},
    {281, {1,2,3,4,5}},
    {281, {1,2,3,4,5}},
    {818, {1,2,3,4,5}},
    {818, {1,2,3,4,5}},
    {819, {1,2,3,4,5}},
    {819, {1,2,3,4,5}},
}
--endregion

--region StartFight/EndFight events

---@param fighters Fighter[]
---@return boolean
local function groupHasBoss(fighters)
    for _, mob in ipairs(fighters) do
        -- mob.grade only exists on MobGrade
        if mob.grade and mob:id() == bossMobId then
            return true
        end
    end
    return false
end

local function respawnBoss()
    -- Pick the next map where we want the boss to spawn
    local respawnMapId = mapIds[math.random(#mapIds)]

    -- Add the boss group
    local actorId = World:map(respawnMapId):spawnGroupDef({-1, bossGroup})
    JLogF("LordCrow: Boss respawned on map #{} ({})", respawnMapId, actorId)
end

local function bossMapId(retried)
    for _, mapId in ipairs(mapIds) do
        for _, group in ipairs(World:map(mapId):mobGroups()) do
            if groupHasBoss(group) then
                return mapId
            end
        end
    end
    if retried then
        JLogF("LordCrow: Failed to find boss, even after retrying, something is very wrong")
        return
    end
    JLogF("LordCrow: Failed to find boss, attempting to respawn")
    respawnBoss()
    return bossMapId(true)
end

---@param md MapDef
---@param _ Map
---@param team1 Fighter[]
---@param team2 Fighter[]
local function onFightStart(md, _, team1, team2)
    -- Check if that fight contained the boss
    if not groupHasBoss(team1) and not groupHasBoss(team2) then return end

    -- Fight against boss has started, we need to make it respawn somewhere
    JLogF("LordCrow: Boss engaged on map #{}", md.id)

    respawnBoss()
end

---@param p Player
---@param isWinner boolean
---@param winners Fighter[]
---@param losers Fighter[]
local function onFightEnd(p, isWinner, winners, losers)
    -- Get info for current map
    local info = mapInfos[p:mapID()]
    if not info then return end -- Should not happen

    -- Check if it contained one of the mob
    for _, mob in ipairs(losers) do
        -- mob.grade only exists on MobGrade
        if mob.grade and mob:id() == downMobId then
            p:teleport(info.down[1], info.down[2])
            return
        elseif mob.grade and mob:id() == rightMobId then
            p:teleport(info.right[1], info.right[2])
            return
        elseif mob.grade and mob:id() == bossMobId then
            p:teleport(9604, 50)
        end
    end
end

--endregion

--region NPC
---@param p Player
local function onTalkInsideDungeon(npc, p, answer)
    if answer == 0 then p:ask(3182, {2809})
    elseif answer == 7655 then p:endDialog()
    elseif answer == 2809 then
        -- Where does Lord Crow hide ?
        local mapInfo = mapInfos[bossMapId()]
        p:ask(mapInfo.dialog, {7655})
    end
end

local defaultOnTalk = NPCS[npcId].onTalk

-- Override NPC's onTalk function
NPCS[npcId].onTalk = function(npc, p, answer)
    local map = p:mapID()
    -- If the NPC is talked to from inside the dungeon, call onTalkInsideDungeon instead
    for _, mapId in ipairs(mapIds) do
        if mapId == map then
            onTalkInsideDungeon(npc, p, answer)
            return
        end
    end
    defaultOnTalk(npc, p, answer)
end


--endregion

--region Add NPC/Fight events/StaticGroup to Maps
for k,v in pairs(mapInfos) do
    local map = MAPS[k]
    if map then
        map.npcs[npcId] = v.npcPos
        map.onFightStart[PVMFightType] = onFightStart
        map.onFightEnd[PVMFightType] = onFightEnd
        map.staticGroups = {
            {v.groupsCells[1], mobGroup1},
            {v.groupsCells[2], mobGroup2}
        }
    else
        JLogF("LordCrow: Map #{} not found", k)
    end
end
--endregion


-- Call bossMapId to trigger boss spawn
bossMapId()
