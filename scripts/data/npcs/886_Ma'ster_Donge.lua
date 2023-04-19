local npc = Npc(886, 90)

npc.colors = {5767327, 61976, 16713222}
npc.accessories = {0, 6481, 2386, 0, 0}
npc.customArtwork = 9086

local questID = 198
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
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
        --TODO: if we dont have the key/keychain the only response is "3359" and if we have one of them responses are 3358/3359
        IncarnamDungeon:onTalkToGateKeeper(p, answer)
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
