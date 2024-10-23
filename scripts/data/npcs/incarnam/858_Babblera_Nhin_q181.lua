local npc = Npc(858, 31)

local questID = 181
local recipeID = 8528
local fedexLocalsQuestID = 180

npc.gender = 1
npc.colors = {16755213, 4073268, 16773017}
npc.accessories = {0, 2531, 2532, 0, 7076}
npc.customArtwork = 9095

npc.quests = {fedexLocalsQuestID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]
    local fedexLocalsQuest = QUESTS[fedexLocalsQuestID]

    if quest:ongoingFor(p) and quest:hasCompletedObjective(p, 745) then
        if answer == 0 then
            if not quest:hasCompletedObjective(p, 744) then
                if p:consumeItem(recipeID, 1) then
                    quest:completeObjective(p, 744)
                end
                p:ask(3659)
                return
            end
            p:ask(3658)
        end
        return
    end

    if fedexLocalsQuest:ongoingFor(p) then
        if fedexLocalsQuest:canCompleteObjective(p, 752) then
            p:ask(3683)
            fedexLocalsQuest:completeObjective(p, 752)
        end
        return
    end

    p:ask(3658)
end

RegisterNPCDef(npc)
