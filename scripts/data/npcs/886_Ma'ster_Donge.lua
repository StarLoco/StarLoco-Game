local npc = Npc(886, 90)

npc.colors = {5767327, 61976, 16713222}
npc.accessories = {0, 6481, 2386, 0, 0}
npc.customArtwork = 9086

local dungeon = IncarnamDungeon
local questID = 198
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasAllItems = dungeon.keyID and dungeon:hasKeyChain(p)
    local hasKey = dungeon.keyID
    if p:mapID() == 10352 then
        if p:questAvailable(questID) and answer == 0 then
            p:ask(3823, {3354, 3353})
        elseif answer == 3354 or answer == 3353 then
            p:ask(3824, {3355})
        elseif answer == 3355 then
            p:ask(3826,{3356})
        elseif answer == 3356 then
            p:startQuest(questID)
            p:endDialog()
        elseif p:questOngoing(questID) then
            --TODO: ADD WHAT HAPPENS IF WE HAVE COMPLETED THE OBJECTIVE
           return p:ask(3847)
        end
        elseif p:questFinished(questID) then
            --TODO: NEED TO ADD DIALOGID WHEN WE SPEAK TO HIM WITH QUEST ALREADY COMPLETED
            p:ask()
    elseif p:mapID() == 10359 then
        if answer == 0 then
            -- Check if p has item / keychain
            local responses = {}
            if p:getItem(dungeon.keyID, 1) or dungeon:hasKeyChain(p) then
                table.insert(responses,dungeon.keychainResponseID)
                table.insert(responses,dungeon.keyResponseID)
            else
                table.insert(responses,dungeon.keyResponseID)
            end
            p:ask(dungeon.questionID, responses)
        elseif answer == dungeon.keychainResponseID then
            if hasAllItems then
                dungeon:useKeyChain(p)
                p:teleport(dungeon.tpDest[1], dungeon.tpDest[2])
                p:endDialog()
            elseif hasKey then
                p:consumeItem(dungeon.keyID, 1)
                p:teleport(dungeon.tpDest[1], dungeon.tpDest[2])
                p:endDialog()
            else
                p:endDialog()
            end
        elseif answer == dungeon.keyResponseID then
            p:teleport(10335,267)
        end
    elseif p:mapID() == 10364 then
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
end

RegisterNPCDef(npc)
