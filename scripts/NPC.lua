NPCS = NPCS or {}

Npc = Npc or {}
Npc.__index = Npc

setmetatable(Npc, {
    __call = function (cls, id, gfxID)
        local self = setmetatable({}, Npc)
        self.id = id
        self.gfxID = gfxID
        self.gender = 0
        self.scaleX = 100
        self.scaleY = 100
        self.colors = {-1,-1,-1}
        self.accessories = {}
        self.extraClip = nil
        self.customArtwork = 0

        -- Register the Npc in the global dict
        if NPCS[self.id] ~= nil then
            JLogF("Overriding Npc #{}", self.id)
        end
        NPCS[self.id] = self
        return self
    end,
})

-- Called by the Dialog class, overwritten by each Npc
function Npc:onTalk(jPlayer, answer) return nil end

---- Used to show the ! on top of the NPC
--function Npc:hasQuestAvailable(jPlayer)
--    -- TODO
--    return false
--end

---- Called by the Map class, allows some NPC to be shown only when player have a specific quest
--function Npc:isVisible(jPlayer, jMap)
--    -- TODO
--    return true
--end


-- Helper called by java side
function onNpcDialog(player, npcID, answer)
    return NPCS[npcID] and NPCS[npcID]:onTalk(player, answer) or nil
end

--
--function Npc:ask(jPlayer, questionID, answerIDs)
--    -- TODO
--end
--
--function Npc:endDialog(jPlayer)
--    -- TODO
--end