local npc = Npc(887, 9083)
local questID = 199

npc.colors = {8355711, -1, -1}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 then
            p:ask(3832, {3363, 3364})
        elseif answer == 3363 then 
            quest:startFor(p, self.id) 
            p:endDialog()
        elseif answer == 3364 then p:endDialog()
        end
        return
    end

    if quest:ongoingFor(p) then
        if quest:tryCompleteBringItemObjectives(p, self.id) then
            p:ask(3844)
            return
        end
        if answer == 0 then
            p:ask(3845)
        end
        return
    end

    if quest:finishedBy(p) then
        p:ask(3845)
        return
    end
    p:endDialog()
end

RegisterNPCDef(npc)
