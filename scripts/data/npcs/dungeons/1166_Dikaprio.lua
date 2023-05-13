local npc = Npc(1166, 1278)

local dungeonKeyId = 7312

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasArtifacts = true
    for i = 7373, 7378 do
        if not p:getItem(i) then
            hasArtifacts = false
            break
        end
    end
    local hasDungeonKeyAndArtifacts = p:getItem(dungeonKeyId) and hasArtifacts
    local hasDungeonKeyChainAndArtifacts = hasKeyChainFor(p, dungeonKeyId) and hasArtifacts

    local showKeyResponse = 6640
    local showKeyChainResponse = 6641

    if answer == 0 then
        if hasDungeonKeyAndArtifacts or hasDungeonKeyChainAndArtifacts then
            local responses = {}
            if hasDungeonKeyAndArtifacts then
                table.insert(responses, showKeyResponse) end
            if hasDungeonKeyChainAndArtifacts then
                table.insert(responses, showKeyChainResponse) end
            p:ask(2942, responses)
        else
            p:ask(2945)
        end
    elseif answer == showKeyResponse and hasDungeonKeyAndArtifacts then
        local consumedAll = p:consumeItem(7373, 1) and
                p:consumeItem(7374, 1) and
                p:consumeItem(7375, 1) and
                p:consumeItem(7376, 1) and
                p:consumeItem(7377, 1) and
                p:consumeItem(7378, 1) and
                p:consumeItem(dungeonKeyId, 1)
        if consumedAll then
            p:teleport(8349, 268)
            p:endDialog()
        end
    elseif answer == showKeyChainResponse and hasDungeonKeyChainAndArtifacts then
        local consumedAll = p:consumeItem(7373, 1) and
                p:consumeItem(7374, 1) and
                p:consumeItem(7375, 1) and
                p:consumeItem(7376, 1) and
                p:consumeItem(7377, 1) and
                p:consumeItem(7378, 1) and
                p:consumeItem(dungeonKeyId, 1)
        if consumedAll then
            p:teleport(8349, 268)
            p:endDialog()
        end
    end
end

RegisterNPCDef(npc)
