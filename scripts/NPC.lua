require("Dungeon")

---@type table<number, Npc>
NPCS = {}

---@class Npc
---@field id number
---@field gfxID number
---@field gender number
---@field scaleX number
---@field scaleY number
---@field colors number[] 3 colors, -1 for default
---@field accessories number[] Up to 5 accessories
---@field customArtwork number
Npc = {}
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
        self.customArtwork = 0
        self.flags = 0

        -- Register the Npc in the global dict
        if NPCS[self.id] ~= nil then
            JLogF("Overriding Npc #{}", self.id)
        end
        NPCS[self.id] = self
        return self
    end,
})

-- Called by the Dialog class, overwritten by each Npc
---@param player Player
---@param answer number
---@return void
function Npc:onTalk(player, answer) return end

---@param player Player
---@return {item:number,price:number,currency:number}[]
function Npc:salesList(player)
    return self.sales or {}
end


---@param player Player
---@param offer ItemStack[]
---@return ItemStack
function Npc:exchangeOutcome(player, offer)
    if not self.exchanges then return {} end

    -- TODO


    return {}
end

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

