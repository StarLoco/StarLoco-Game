local npc = Npc(777, 9031)

npc.accessories = {0, 0x1b12, 0, 0, 0}

local dungeon = TofuDungeon
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3174, {2796})
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

RegisterNPCDef(npc)
