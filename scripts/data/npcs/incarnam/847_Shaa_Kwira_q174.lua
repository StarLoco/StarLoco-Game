local npc = Npc(847, 111)

local questID = 174

--TODO: Lié à la quête 174
npc.colors = {2133067, 13458604, 16175502}
npc.accessories = {3650, 7142, 1695, 0, 0}
npc.customArtwork = 9085
npc.gender = 1

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 then p:ask(3573, {3156, 3158})
        elseif answer == 3156 or answer == 3160 or answer == 3162 or answer == 3164 then p:endDialog()
        elseif answer == 3158 then p:ask(3575, {3159, 3160})
        elseif answer == 3159 then
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end

    if quest:ongoingFor(p) then
        p:ask(3676)
        return
    end

    if quest:finishedBy(p) then
        p:ask(3578)
        return
    end
end

RegisterNPCDef(npc)
