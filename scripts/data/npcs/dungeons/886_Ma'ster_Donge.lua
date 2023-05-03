local npc = Npc(886, 90)
local questID = 198

npc.colors = {5767327, 61976, 16713222}
npc.accessories = {0, 6481, 2386, 0, 0}
npc.customArtwork = 9086

local dungeonKeyId = 8545
local enterDungeonDest = {10360, 364}

---@param p Player
---@param answer number
local function talkMapDungeonEntrance(p, answer)
    local showKeyResponse = 3358
    local hasDungeonKeys = p:getItem(dungeonKeyId) or hasKeyChainFor(p, dungeonKeyId)

    if answer == 0 then
        local responses = hasDungeonKeys and {showKeyResponse, 3359} or {3359}
        p:ask(3828, responses)
    elseif answer == showKeyResponse then
        if hasDungeonKeys then
            if not useKeyChainFor(p, dungeonKeyId) and not p:consumeItem(dungeonKeyId, 1) then
                -- Should not happen (cheat?)
                p:endDialog()
                return
            end
            p:teleport(enterDungeonDest[1], enterDungeonDest[2])
            p:endDialog()
        else
            p:endDialog()
        end
    elseif answer == 3359 then
        p:teleport(10335,267)
        p:endDialog()
    end
end

local function talkMapDungeonExit(p, answer)
    if answer == 0 then
        p:ask(3829, {3360, 3361})
    elseif answer == 3360 then
        p:teleport(10335, 267)
        p:endDialog()
    elseif answer == 3361 then
        p:teleport(10354, 297)
        p:endDialog()
    end
end

local onTalkMaps = {
    [10359] = talkMapDungeonEntrance,
    [10364] = talkMapDungeonExit
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]
    if onTalkMaps[p:mapID()] then
        onTalkMaps[p:mapID()](p, answer)
    end
    if p:mapID() == 10352 then
        if quest:availableTo(p) and answer == 0 then
            p:ask(3823, {3354, 3353})
        elseif answer == 3354 or answer == 3353 then
            p:ask(3824, {3355})
        elseif answer == 3355 then
            p:ask(3826,{3356})
        elseif answer == 3356 then
            quest:startFor(p, self.id)
            p:endDialog()
        elseif quest:ongoingFor(p) then
            --TODO: ADD WHAT HAPPENS IF WE HAVE COMPLETED THE OBJECTIVE
            return p:ask(3847)
        end
    elseif quest:finishedBy(p) then
        --TODO: NEED TO ADD DIALOGID WHEN WE SPEAK TO HIM WITH QUEST ALREADY COMPLETED
        p:ask()
    end
end

RegisterNPCDef(npc)
