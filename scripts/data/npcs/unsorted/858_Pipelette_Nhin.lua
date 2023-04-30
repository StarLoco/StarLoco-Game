local npc = Npc(858, 31)

local questID = 181
local recipeID = 8528

npc.gender = 1
npc.colors = {16755213, 4073268, 16773017}
npc.accessories = {0, 2531, 2532, 0, 7076}
npc.customArtwork = 9095

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:ongoingFor(p) then
        if answer == 0 and p:consumeItem(recipeID, 1) then
            p:ask(3659)
            quest:completeObjective(p, 744)
        end
        return
    end
    p:ask(3658)
end

RegisterNPCDef(npc)
