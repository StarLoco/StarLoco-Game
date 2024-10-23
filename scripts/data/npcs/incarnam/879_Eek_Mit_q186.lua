local npc = Npc(879, 41)

local questID = 189

npc.gender = 1
npc.colors = {16767910, 16709884, 3169697}
npc.accessories = {0, 7226, 957, 0, 0}
npc.customArtwork = 9093

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    -- Vérifie si la quête zaapQuest est terminée
    if quest:finishedBy(p) then
            p:ask(3794)
        return
    end

    -- Si la quête est disponible mais manque un prerequis
    if quest:availableTo(p) then
        if answer == 0 then
            p:ask(3791, {3328, 3329})
        elseif answer == 3328 then
            p:ask(3792, {3330})
        elseif answer == 3329 then
            p:endDialog()
        elseif answer == 3330 then
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if quest:ongoingFor(p) then
        if answer == 0 then
            local friends = p:account():friends()
            if #friends > 0 then
                quest:completeObjective(p, 782)
            end
            if quest:finishedBy(p) then
                p:ask(3793)
            else 
                p:ask(3795)
            end
        
        end
        return
    end

    if answer == 0 then 
        p:ask(3794)
    end
end

RegisterNPCDef(npc)
