local npc = Npc(941, 11)

npc.gender = 1
npc.colors = {1754595, 16600321, 11664131}
npc.accessories = {0, 8848, 0, 0, 0}

local dungeonKeyId = 8917
local enterDungeonDest = {10360, 364}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(dungeonKeyId)
    local hasKeyChain = hasKeyChainFor(p, dungeonKeyId)
    if p:mapID() == 10722 then
        local showKeyResponse = 6634
        local showKeyResponse2 = 6620

        if answer == 0 then
            local responses = {}
            if hasDungeonKey then
                table.insert(responses, showKeyResponse)
            end
            if hasKeyChain then
                table.insert(responses, showKeyResponse2)
            end
            p:ask(4190, responses)
        elseif answer == showKeyResponse or showKeyResponse2 then
            p:teleport(enterDungeonDest[1], enterDungeonDest[2])
            p:endDialog()
        end
    elseif p:mapID() == 11234 then
        local hasItems = hasDungeonKey or hasKeyChain
        local showKeyResponse = 3669
            if answer == 0 then
                local responses = hasItems and {showKeyResponse} or {}
                p:ask(4191, responses)
        elseif answer == showKeyResponse then
            if not useKeyChainFor(p, dungeonKeyId) and not p:consumeItem(dungeonKeyId, 1) then
                -- Should not happen (cheat?)
                p:endDialog()
                return
            end
                p:addItem(8971)
                p:addItem(8671)
                p:teleport(10722, 329)
                p:endDialog()
            else
                p:endDialog()
            end
        end
    end

RegisterNPCDef(npc)
