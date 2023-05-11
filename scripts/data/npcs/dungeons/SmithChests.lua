local npcs = {
    {npcId = 1278, gfxId = 9218, diceRollSId = 94, chestOpenedSId = 98},
    {npcId = 1279, gfxId = 9219, diceRollSId = 95, chestOpenedSId = 99},
    {npcId = 1280, gfxId = 9220, diceRollSId = 96, chestOpenedSId = 100},
    {npcId = 1281, gfxId = 9221, diceRollSId = 97, chestOpenedSId = 101}
}

---@param p Player
local function onChestOpened(p, succeed)
    if not succeed then
        return
    end
    p:teleport(1113, 213)
end

---@param p Player
local function onDiceRoll(chestOpenedSId)
    return function(p, succeed)
        if not succeed then
            return
        end
        p:addItem(11525)
        p:sendInfoMsg(0, 207)
        p:startScenario(chestOpenedSId, "7001010000", onChestOpened)
    end
end

local function onTalk(diceRollSId, chestOpenedSId)
    ---@param p Player
    ---@param answer number
    return function(self, p, answer)
        if answer == 0 then
            p:ask(7674, {7695, 7696})
        elseif answer == 7695 then
            --p:pauseDialog()
            p:endDialog()
            p:startScenario(diceRollSId, "7001010000", onDiceRoll(chestOpenedSId))
        elseif answer == 7696 then
            p:teleport(1113, 199)
            p:endDialog()
        end
    end
end

for _, info in ipairs(npcs) do
    local npc = Npc(info.npcId, info.gfxId)
    npc.onTalk = onTalk(info.diceRollSId, info.chestOpenedSId)
    RegisterNPCDef(npc)
end
