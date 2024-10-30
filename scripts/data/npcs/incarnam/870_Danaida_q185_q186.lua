local npc = Npc(870, 9044)

local waterQuestID = 185

npc.gender = 1

npc.quests = {waterQuestID} -- On définit la quête pour ce PNJ

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local waterQuest = QUESTS[waterQuestID]

    -- Si la quête "waterQuest" est disponible pour le joueur
    if waterQuest:availableTo(p) then
        if answer == 0 then
            p:ask(3718, {3258, 3257}) -- debut du dialogue avec deux réponses possible
        elseif answer == 3257 then
            p:endDialog() -- Fermer le dialogue et refuse la quête
        elseif answer == 3258 then
            waterQuest:startFor(p, self.id) -- Démarrer la quête si le joueur accepte
            p:endDialog()
        end
        return
    end

    -- Si la quête "waterQuest" est en cours
    if waterQuest:ongoingFor(p) then
        if waterQuest:tryCompleteBringItemObjectives(p, self.id) then
            p:ask(3719, {3259})
        else
            p:ask(3720) -- Si les objets manquent, donne une indication ou les trouver
        end
        return
    end

    -- Gérer la réponse après que le joueur a cliqué sur 3259
    if answer == 3259 then
        p:endDialog() -- Fermer le dialogue et terminer la quête
        return
    end

    -- Si la deux quête est terminée
    if waterQuest:finishedBy(p) then
        p:ask(3721) -- Dialogue final une fois que la quête est terminée
        return
    end
end

RegisterNPCDef(npc)
