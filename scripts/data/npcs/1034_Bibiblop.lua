local npc = Npc(1034, 110)

npc.colors = {16055037, -1, 2403546}
npc.accessories = {0, 0x24b3, 0, 0, 0}

---@param p Player
function npc:onTalk(p, answer)
    if p:mapID() == 11878 then
        BlopDungeon:onTalkToGateKeeper(p, answer)
    elseif p:mapID() == 11891 then 
        if answer == 0 then
            local responses = {}
            local dungeon = RainbowBlopDungeon
            if p:getItem(dungeon.keyID, 1) then table.insert(responses, dungeon.keyResponseID) end
            if dungeon:hasKeyChain(p) then table.insert(responses, dungeon.keychainResponseID) end
            table.insert(responses, 4540)
            p:ask(dungeon.questionID, responses)
        elseif answer == RainbowBlopDungeon.keyResponseID then
            -- Use Key then teleport
            if p:consumeItem(RainbowBlopDungeon.keyID, 1) then p:teleport(RainbowBlopDungeon.tpDest[1], RainbowBlopDungeon.tpDest[2]) end
            p:endDialog()
        elseif answer == RainbowBlopDungeon.keychainResponseID then
            -- Use Keychain then teleport
            if RainbowBlopDungeon:useKeyChain(p) then p:teleport(RainbowBlopDungeon.tpDest[1], RainbowBlopDungeon.tpDest[2]) end
            p:endDialog()
        elseif answer == 4540 then
            p:teleport(11878, 99)
            p:endDialog()
        end
    elseif p:mapID() == 11886 then
        if answer == 0 then
            p:ask(5403, {4532, 4533, 4534, 4535})
        elseif answer == 4532 then
            p:teleport(11887, 463)
            p:endDialog()
        elseif answer == 4533 then
            p:teleport(11888, 463)
            p:endDialog()
        elseif answer == 4534 then
            p:teleport(11889, 463)
            p:endDialog()
        elseif answer == 4535 then
            p:teleport(11890, 463)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)