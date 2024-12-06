local npc = Npc(869, 1212)

local questID = 184

--TODO: Lié à la quête 184
npc.gender = 1

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 or answer == 3265 then p:ask(3710, {3260, 3262})
        elseif answer == 3262 then p:ask(3724, {3269, 3265})
        elseif answer == 3269 or answer == 3260 then p:ask(3723, {3264, 3263})
        elseif answer == 3264 then p:endDialog()
        elseif answer == 3263 then p:ask(3725, {3266})
        elseif answer == 3266 then
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if quest:ongoingFor(p) then
        if quest:tryCompleteBringItemObjectives(p, self.id) then
            p:ask(3722)
            return
        end
        p:ask(3712)
        return
    end

    if quest:finishedBy(p) then
        p:ask(3711)
        return
    end
end

RegisterNPCDef(npc)
