local npc = Npc(754, 1363)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local dungeon = KoolichDungeon
    local cell = p:cell()
    local orientation = p:orientation()

    if not (cell == 213 and orientation == 5) then
        p:ask(3161)
        return
    end

    if answer == 0 then
        local responses = {}
        if p:getItem(dungeon.keyID, 1) then table.insert(responses, dungeon.keyResponseID) end
        if dungeon:hasKeyChain(p) then table.insert(responses, dungeon.keychainResponseID) end
        table.insert(responses, 2743)
        p:ask(dungeon.questionID, responses, "[name]")
    elseif answer == dungeon.keyResponseID then
        -- Use Key then teleport
        if p:consumeItem(dungeon.keyID, 1) then p:teleport(dungeon.tpDest[1], dungeon.tpDest[2]) end
        p:endDialog()
    elseif answer == dungeon.keychainResponseID then
        -- Use Keychain then teleport
        if dungeon:useKeyChain(p) then p:teleport(dungeon.tpDest[1], dungeon.tpDest[2]) end
        p:endDialog()
    elseif answer == 2743 then
        p:ask(3210)
    end
end

RegisterNPCDef(npc)
