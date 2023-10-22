local npc = Npc(880, 20)

npc.colors = {5855514, 16777215, 9847110}
npc.accessories = {0, 0, 8534, 0, 0}
npc.customArtwork = 9103

local questID = 191

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) or quest:ongoingFor(p) then
        if answer == 0 then p:ask(3798, {3335, 3334})
        elseif answer == 3334 then p:endDialog()
        elseif answer == 3335 then p:ask(3801, {3333})
        elseif answer == 3333 then
            if quest:availableTo(p) then 
                quest:startFor(p, self.id)
            end
            p:endDialog()
            p:forceFight({-1, {{1002, {2}}}})
        end
        return
    end

    if answer == 0 then p:ask(3798, {3331, 3332})
    elseif answer == 3331 then p:endDialog()
    elseif answer == 3332 then p:ask(3801, {3333})
    end

    if quest:finishedBy(p) then
        p:ask(3799)
        return
    end
end

RegisterNPCDef(npc)
