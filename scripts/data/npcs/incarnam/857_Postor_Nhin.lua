local npc = Npc(857, 30)

local recipeQuestID = 181
local recipeID = 8528
local zaapQuestID = 182

npc.colors = {6513587, 16771366, 13749677}
npc.accessories = {0, 0x1b4c, 0, 0, 0}
npc.customArtwork = 9089

npc.quests = {zaapQuestID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local recipeQuest = QUESTS[recipeQuestID]
    local zaapQuest = QUESTS[zaapQuestID]

    if recipeQuest:availableTo(p) then
        p:ask(3654)
        return
    end

    if recipeQuest:ongoingFor(p) then
        if answer == 0 then
            if recipeQuest:hasCompletedObjective(p, 745) then
                if not p:getItem(recipeID) then
                    p:addItem(recipeID)
                end
                p:compassTo(10286)
                p:ask(3653)
                return
            end
            p:ask(3655, {3223})
        elseif answer == 3223 then
            p:ask(3656, {3224, 3225})
        elseif answer == 3224 then
            recipeQuest:completeObjective(p, 745)
            p:ask(3657, {3226})
        elseif answer == 3225 then
            p:endDialog()
        elseif answer == 3226 then
            if not p:getItem(recipeID) then
                p:addItem(recipeID)
            end
            p:endDialog()
        end
        return
    end

    if recipeQuest:finishedBy(p) then
        if answer == 0 then
            p:ask(3660, {3228; 3227})
        elseif answer == 3227 then
            p:endDialog()
        elseif answer == 3228 then
            p:ask(3661, {3229})
        elseif answer == 3229 then
            p:ask(3662, {3230})
        elseif answer == 3230 then
            p:ask(3663, {3231})
        elseif answer == 3231 then
            p:ask(3664)
            zaapQuest:startFor(p, self.id)

        end
    end
end

RegisterNPCDef(npc)
