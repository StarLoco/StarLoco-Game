--- Dopple master NPCs
--- Start dopple quests, fights and trade doploons
---
---
--- On Official server, each NPC has its own response IDs

local classBangQuestID = 470

local templeNpcInfo = {
    [FecaBreed]     = {npc=433, quest=469,  init=1769, info=1420, train=216,  trade=6753, classBang=6775},
    [IopBreed]      = {npc=434, quest=460,  init=1771, info=1424, train=1559, trade=6754, classBang=6776},
    [EniripsaBreed] = {npc=435, quest=462,  init=1768, info=1535, train=6747, trade=6751, classBang=6773},
    [OsamodasBreed] = {npc=436, quest=461,  init=1766, info=1418, train=1485, trade=6755, classBang=6777},
    [XelorBreed]    = {npc=437, quest=468,  init=1758, info=168,  train=1415, trade=6761, classBang=6782},
    [EcaflipBreed]  = {npc=438, quest=459,  init=1772, info=1425, train=1515, trade=6750, classBang=6769},
    [CraBreed]      = {npc=439, quest=458,  init=1767, info=1419, train=1509, trade=6697, classBang=6772},
    [EnutrofBreed]  = {npc=440, quest=464,  init=1764, info=1417, train=172,  trade=6752, classBang=6774},
    [SramBreed]     = {npc=441, quest=467,  init=1773, info=1426, train=1575, trade=6760, classBang=6781},
    [SadidaBreed]   = {npc=442, quest=465,  init=1770, info=1422, train=1581, trade=6759, classBang=6780},
    [SacrierBreed]  = {npc=443, quest=463,  init=1799, info=1519, train=1520, trade=6758, classBang=6779},
    [PandawaBreed]  = {npc=672, quest=466,  init=2851, info=6757, train=6429, trade=6598, classBang=6778},
}

---@return boolean true if matching
local function makeTradeMenu(npcInfo)
    return function(p, answer)
        if answer == npcInfo.trade then
            p:endDialog() -- FIXME
        end
        return false
    end
end

---@return boolean true if matching
local function makeInfoMenu(npcInfo)
    return function(p, answer)
        if answer == npcInfo.info then
            p:endDialog() -- FIXME
        end
        return false
    end
end

function onTalkDoppleMaster(npcInfo)
    local infoMenu = makeInfoMenu(npcInfo)
    local tradeMenu = makeTradeMenu(npcInfo)

    return function(self, p, answer)
        local doppleQuest = QUESTS[npcInfo.quest]
        local classBangQuest = QUESTS[classBangQuestID]

        if answer == 0 then
            local responses = {npcInfo.info, npcInfo.trade}
            if doppleQuest:availableTo(p) or doppleQuest:ongoingFor(p) then table.insert(responses, npcInfo.train) end
            if classBangQuest:availableTo(p) then table.insert(responses, npcInfo.classBang) end
            p:ask(npcInfo.init, responses)
        elseif answer == npcInfo.classBang then
            -- TODO: add intermediary dialog
            p:endDialog()
            classBangQuest:startFor(p, self.id)
        elseif answer == npcInfo.train then
            if not doppleQuest:startFightFor(p, self.id) then
                error("cheat attempt")
            end
        elseif infoMenu(p, answer) then return
        elseif tradeMenu(p, answer) then  return
        end
    end
end

table.insert(POST_INITS, function()
    for _, npcInfo in pairs(templeNpcInfo) do
        local npc = NPCS[npcInfo.npc]
        npc.onTalk = onTalkDoppleMaster(npcInfo)
        npc.quests = {npcInfo.quest}
    end
end)
