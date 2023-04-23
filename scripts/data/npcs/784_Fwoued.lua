local npc = Npc(784, 50)

npc.colors = {3248109, 16724531, 16773874}
npc.accessories = {0, 8285, 8281, 0, 0}

---@param p Player
---@param answer number
local dungeon = DragonPigDungeon
function npc:onTalk(p, answer)
    local hasAllItems = p:getItem(7511) and p:getItem(8320)
    local hasAllItems2 = p:getItem(8320) and dungeon:hasKeyChain(p)
    local hasAllItems3 = p:getItem(8320) and p:getItem(7511) and dungeon:hasKeyChain(p)
    local labyrinthMapsIDs = {9371, 9372, 9373, 9374, 9375, 9376, 9377, 9378, 9379, 9380, 9381, 9382, 9383, 9384, 9385, 9386, 9387, 9388, 9389, 9390, 9391, 9392, 9393, 9394}
    if p:mapID() == 9397 then
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
            p:teleport(9396, 387)
            p:endDialog()
        elseif answer == dungeon.keyResponseID then
            p:ask(3217, {2850})
        elseif answer == 2850 then
            p:ask(3218, {2851})
        elseif answer == 2851 then
            p:ask(3219, {2857})
        elseif answer == 2857 then
            p:ask(3224, {2858})
        elseif answer == 2858 then
            p:ask(3225, {2859})
        elseif answer == 2859 then
            p:ask(3226, {2860})
        elseif answer == 2860 then
            p:ask(3227, {2861})
        elseif answer == 2861 then
            p:ask(3228)
        end
    elseif p:mapID() == 9396 then
        if answer == 0 then
            p:ask(3221, {2854})
        elseif answer == 2854 then
            p:ask(3222, {2855})
        elseif answer == 2855 then
            p:teleport(9397, 90)
            p:endDialog()
        end
        elseif table.contains(labyrinthMapsIDs, p:mapID()) then
        if answer == 0 then
            p:ask(3220, {2853, 2852})
        elseif answer == 2853 then
            p:teleport(9396, 387)
            p:endDialog()
        elseif answer == 2852 then
            p:ask(3217, {2850})
        elseif answer == 2850 then
            p:ask(3218, {2851})
        elseif answer == 2851 then
            p:ask(3219, {2857})
        elseif answer == 2857 then
            p:ask(3224, {2858})
        elseif answer == 2858 then
            p:ask(3225, {2859})
        elseif answer == 2859 then
            p:ask(3226, {2860})
        elseif answer == 2860 then
            p:ask(3227, {2861})
        elseif answer == 2861 then
            p:ask(3228)
        end
    elseif p:mapID() == 9395 then
        if answer == 0 then
            if hasAllItems or hasAllItems2 or hasAllItems3 then
                p:ask(3223, {2856})
            else
                p:ask(3223)
            end
        elseif answer == 2856 then
            if hasAllItems3 then
                p:consumeItem(8320, 1)
                dungeon:useKeyChain(p)
                p:teleport(8541, 336)
                p:endDialog()
            elseif hasAllItems2 then
                p:consumeItem(8320, 1)
                dungeon:useKeyChain(p)
                p:teleport(8541, 336)
                p:endDialog()
            elseif hasAllItems then
                p:consumeItem(7511, 1)
                p:consumeItem(8320, 1)
                p:teleport(8541, 336)
                p:endDialog()
            else
                p:endDialog()
            end
        end
    elseif p:mapID() == 9989 then
        if answer == 0 then
            p:ask(3231, {2855})
        elseif answer == 2855 then
            p:teleport(9397,90)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
