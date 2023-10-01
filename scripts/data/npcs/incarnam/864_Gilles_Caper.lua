local npc = Npc(864, 9039)

local unlearnJobs = function (p)
    local jobs = p:jobs ()
    for _, jobID in ipairs (jobs) do
        if not p:tryUnlearnJob(jobID) then
            error("Failed to Unlearn Job")
            p:endDialog()
            return
        end
    end
    p:endDialog()
end

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasJobs = # (p:jobs()) > 0
    if answer == 0 then
        if not hasJobs then
            p:ask(3699)
            return
        end
        p:ask(3698, {3241, 3242})
    elseif answer == 3241 then p:ask(3700, {3243})
    elseif answer == 3243 then
        return unlearnJobs(p)
    elseif answer == 3242 then p:endDialog()
    end
end

RegisterNPCDef(npc)
