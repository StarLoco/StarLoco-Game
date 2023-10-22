local npc = Npc(506, 30)

local questID = 45

npc.colors = {11448455, -1, 8086352}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 then p:ask(2216, {1859})
        elseif answer == 1859 then p:ask(2217, {1959})
        elseif answer == 1959 then p:ask(2353, {1960})
        elseif answer == 1960 then p:ask(2354, {2106})
        elseif answer == 2106 then 
            p:endDialog()
            quest:completeObjective(p, 306)
        end
        return
    end

    if quest:ongoingFor(p) then
        p:ask(2215)
        return
    end

    if quest:finishedBy(p) then
        p:ask(2215)
        return
    end

end

RegisterNPCDef(npc)
