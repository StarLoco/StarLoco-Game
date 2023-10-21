local npc = Npc(871, 81)

local questID = 186

npc.gender = 1
npc.colors = {16777215, 16737555, 14635481}
npc.accessories = {4241, 7921, 0, 1728, 0}
npc.customArtwork = 9094

npc.quests = {questID}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 then p:ask(3726, {3268, 3267})
        elseif answer == 3268 or answer == 3271 then p:endDialog()
        elseif answer == 3267 then p:ask(3727, {3270, 3271})
        elseif answer == 3270 then p:ask(3728, {3272})
        elseif answer == 3272 then
            quest:startFor(p, self.id)
            p:endDialog()
        end
        return
    end
end

RegisterNPCDef(npc)
