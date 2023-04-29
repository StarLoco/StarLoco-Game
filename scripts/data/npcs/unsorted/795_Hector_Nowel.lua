local npc = Npc(795, 30)

npc.colors = {16711680, 16777215, -1}
npc.accessories = {0, 8334, 8333, 0, 0}


local nowel2KeyID = 9460
local nowel3KeyID = 9459

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if p:mapID() == 9468 then
        if answer == 0 then p:ask(3254, {2896})
        elseif answer == 2896 then p:ask(3257, {2897})
        elseif answer == 2897 then p:ask(3258, {2898, 2901})
        elseif answer == 2898 then p:ask(3259, {2900})
        elseif answer == 2900 then
            p:teleport(9979,213)
            p:endDialog()
        elseif answer == 2901 then p:ask(3260)
        end
    elseif p:mapID() == 9979 then
        if answer == 0 then p:ask(3255, {2902, 2904})
        elseif answer == 2902 then p:ask(3261, {2903})
        elseif answer == 2903 then
            p:teleport(9468,185)
            p:endDialog()
        elseif answer == 2904 then
            p:ask(3262)
        end
    elseif p:mapID() == 9946 then
        local hasNowel2Key = p:getItem(nowel2KeyID)
        local hasNowel3Key = p:getItem(nowel3KeyID)
        local showKeyResponse = 4614
        local showKeyResponse2 = 4615

        if answer == 0 then
            local responses = {4613}
            if hasNowel2Key then
                table.insert(responses, showKeyResponse)
                if hasNowel3Key then
                    table.insert(responses, showKeyResponse2)
                end
            end
            table.insert(responses, 4616)
            p:ask(5494, responses)
        elseif answer == 4613 then
            p:teleport(9981, 375)
            p:endDialog()
        elseif answer == showKeyResponse then
            if hasNowel2Key then
                p:teleport(11943, 375)
                p:endDialog()
            else
                p:endDialog()
            end
        elseif answer == showKeyResponse2 then
            if hasNowel3Key then
                p:teleport(11953, 375)
                p:endDialog()
            else
                p:endDialog()
            end
        elseif answer == 4616 then
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
