local npc = Npc(1223, 71)

npc.gender = 1
npc.accessories = {0, 706, 0, 7704, 0}
npc.quests = {1054} -- Only used to always show a ! on top of her, just like on official servers

-- On official server, this NPC speaks about reset orbs if the player selects "previous" once, which is unrelated.
-- It seems official servers share the same dialogs between Fairy Sette and breed temple NPCs, as they both reset stats.
-- As official servers cannot change the result of selecting a specific answerID based on the NPC,
-- they would have to duplicate the text and create a different answer.
--
-- StarLoco scripts allows us to cleanly do things, so that's what we do here.

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(5452, {7491, 7490})
    -- Reset stat menu
    elseif answer == 7490 or answer == 8208 or answer == 8209 then p:ask(7508, {7328, 7327})
    -- first power
    elseif answer == 7327 then p:ask(7466, {7261, 8209})
    -- first power confirmation
    elseif answer == 7261 then p:ask(7468, {7263})
    -- first power use
    elseif answer == 7263 then
        p:resetStats(true)
        p:endDialog()
    -- second power
    elseif answer == 7328 then p:ask(7467, {7262, 8208})
    -- second power confirmation
    elseif answer == 7262 then p:ask(7469, {7264})
    -- second power use
    elseif answer == 7264 then
        p:resetStats(false)
        p:endDialog()
    -- Reset Spell
    elseif answer == 7491 then
        p:spellResetPanel()
        p:endDialog()
    end
end

RegisterNPCDef(npc)
