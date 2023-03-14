local npc = Npc(870, 9044)

npc.gender = 1

local questID = 185
---@param player Player
function npc:onTalk(player, answer)
    if player:questAvailable(questID) and answer == 0 then player:ask(3718, {3258,3257})
    elseif answer == 3258 then player:startQuest(questID) player:endDialog()
    elseif player:questOngoing(questID) then
        -- If we have the required items, complete objective
        if player:consumeItem(311, 4) and player:completeObjective(questID, 758) then
            player:endDialog()
            return
        end
        -- Ongoing quest dialog
        return player:ask(3720, {})
    elseif player:questFinished(questID) then player:ask(3719, {})
    -- elseif answer == 3257 then player:endDialog()
    else player:endDialog() end
end

RegisterNPCDef(npc)
