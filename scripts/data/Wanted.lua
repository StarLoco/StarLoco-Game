local npcID = 606


local wantedInfo = {
    -- Akornadikt
    {response=2113, item=6875, doc=70, quest=34, step=111, objective=207, kamas=200, minLevel=1},
    -- Frakacia
    {response=2109, item=6871, doc=63, quest=30, step=119, objective=213, kamas=2000, minLevel=7},
    -- RokGnorok
    {response=2550, item=7351, doc=93, quest=115, step=201, objective=421, kamas=6000, minLevel=20},
    -- Brumen
    {response=2112, item=6874, doc=68, quest=33, step=113, objective=209, kamas=33600, minLevel=30},
    -- Qil Bil  -- FIXME Wrong objective
    {response=2111, item=6873, doc=67, quest=32, step=115, objective=425, kamas=19200, minLevel=32},
    -- Aermyne
    {response=2554, item=7350, doc=100, quest=119, step=206, objective=431, kamas=16000, minLevel=32},
    -- Ogivol
    {response=2114, item=6876, doc=96, quest=35, step=109, objective=313, kamas=32000, minLevel=41},
    -- Marzwel
    {response=2552, item=7353, doc=96, quest=117, step=203, objective=425, kamas=25000, minLevel=50},
    -- Musha
    {response=2551, item=7352, doc=94, quest=116, step=202, objective=423, kamas=64000, minLevel=61},
    -- Nomarrow
    {response=2108, item=6870, doc=61, quest=29, step=121, objective=215, kamas=72500, minLevel=81},
    -- Zatoishwan
    {response=2553, item=7354, doc=98, quest=118, step=204, objective=428, kamas=112500, minLevel=91},
}

--region Quest
local createQuest = function(info)
    local qs = QuestStep(info.step)
    local q = Quest(info.quest, {qs})

    qs.objectives = {
        GenericQuestObjective(info.objective, {})
    }
    qs.rewardFn = QuestBasicReward(0, info.kamas)

    q.availableTo = questRequirements(info.minLevel)

    q.startFromDocHref = {info.doc}
end
--endregion

-- Define quests
for _, info in pairs(wantedInfo) do
    createQuest(info)
end

-- Override guard NPC dialog
NPCS[npcID].onTalk = function(_, p, answer)
    local initResponses = {}

    for _, info in ipairs(wantedInfo) do
        local hasWantedNpc = p:gearAt(RolePlayBuffSlot) and p:gearAt(RolePlayBuffSlot):id() == info.item

        if answer == info.response then
            -- Player wants to get their reward
            local quest = QUESTS[info.quest]

            if not p:consumeItem(info.item, 1) then
                error("hack attempt ?")
                return
            end

            if quest:ongoingFor(p) then
                quest:completeObjective(p, info.objective)
            end
            p:ask(2906)
            return
        end

        if answer == 0 and hasWantedNpc then
            table.insert(initResponses, info.response)
        end
    end

    if answer ~= 0 then
        error("hack attempt ?")
        return
    end

    p:ask(2450, initResponses)
end
