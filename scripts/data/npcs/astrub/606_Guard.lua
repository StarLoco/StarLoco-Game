local npc = Npc(606, 1211)

local wantedResponseQuestObjectives = {
    {response=2552, item=7353, quest=117, objective=425}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local initResponses = {}

    for _, info in ipairs(wantedResponseQuestObjectives) do
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

RegisterNPCDef(npc)
