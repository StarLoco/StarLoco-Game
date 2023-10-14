local npc = Npc(846, 80)

local questID = 173

--TODO: Lié à la quête 173
npc.colors = {16760576, 13055806, 10627125}
npc.accessories = {8099, 629, 2387, 0, 7080}
npc.customArtwork = 9087

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 then p:ask(3567, {3150, 3151})
        elseif answer == 3150 then p:ask(3568, {3152, 3153})
        elseif answer == 3151 or answer == 3153 then p:endDialog()
        elseif answer == 3152 then p:ask(3569, {3154})
        elseif answer == 3154 then 
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if quest:ongoingFor(p) then
        if quest:tryCompleteBringItemObjectives(p, self.id) then
            p:ask(3571)
            return
        end

        -- Ongoing quest dialog
        if quest:currentStepFor(p).id == 346 then
            p:ask(3685)
            return
        end
        p:ask(3678)
        return
    end

    if quest:finishedBy(p) then
        p:ask(3570)
        return
    end
end

RegisterNPCDef(npc)
