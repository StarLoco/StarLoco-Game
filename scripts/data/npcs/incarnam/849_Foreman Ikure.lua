local npc = Npc(849, 9037)

local learnJob = function (p, jobID, itemID)
    if not p:tryLearnJob(jobID) then
        p:endDialog()
        error("Failed to Learn Job")
        return
    end
    p:addItem(itemID)
    p:ask(3603)
end

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasJobs = # (p:jobs()) > 0
    if answer == 0 then
        if hasJobs then
            p:ask(3752)
            return
        end
        p:ask(3596, {3182, 3183})
    elseif answer == 3188 then p:ask(3602)
    elseif answer == 3184 then p:ask(3598, {3189})
    elseif answer == 3185 then p:ask(3599, {3190})
    elseif answer == 3186 then p:ask(3600, {3191})
    elseif answer == 3187 then p:ask(3601, {3192})
    elseif answer == 3182 then p:ask(3597, {3184, 3188, 3187, 3185, 3186})
    elseif answer == 3189 then
        return learnJob(p, LumberjackJob, 8539)
    elseif answer == 3190 then
        return learnJob(p, FarmerJob, 8540)
    elseif answer == 3191 then
        return learnJob(p, FishermanJob, 8541)
    elseif answer == 3192 then
        return learnJob(p, AlchemistJob, 8542)
    elseif answer == 3183 then p:endDialog()
    end
end

RegisterNPCDef(npc)
