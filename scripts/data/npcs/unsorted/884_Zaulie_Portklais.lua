local npc = Npc(884, 121)
local questID = 193
local fightObjectiveId = 793

npc.gender = 1
npc.colors = {-1, 13885285, -1}
npc.accessories = {0, 0x1ecf, 0x1ecc, 0x1fd7, 0}
npc.quests = {questID}

---@param p Player
local function startFight(p)
    p:endDialog()
    p:forceFight({-1, {
        {984, {1}}
    }})
end

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local quest = QUESTS[questID]

    if quest:availableTo(p) then
        if answer == 0 then
            p:ask(3813, {3349, 3348})
        elseif answer == 3348 then
            p:endDialog()
        elseif answer == 3349 then
            p:endDialog()
            quest:startFor(p, self.id)
            startFight(p)
        end
        return
    end

    if quest:ongoingFor(p) then
        if answer == 0 then
            if quest:hasCompletedObjective(p, fightObjectiveId) then
                p:ask(3815)
                quest:completeObjective(p, 794)
            else
                p:ask(3816, {3350, 3351})
            end
        elseif answer == 3350 then
            startFight(p)
        elseif answer == 3351 then
            p:endDialog()
        end
        return
    end

    if quest:finishedBy(p) then
        p:ask(3818)
        return
    end
end

RegisterNPCDef(npc)
