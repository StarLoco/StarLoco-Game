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
---@field sales SaleOffer[] optional
---@field barters {to:ItemStack, from:ItemStack[]}[] optional
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
        self.sales = {}
        self.barters = {}

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
---@return SaleOffer[]
function Npc:salesList(player)
    return self.sales
end

-- Default behavior for NPC barter.
-- Assumption: self.barters element are sorted by itemID ASC
---@param player Player
---@param offer ItemStack[]
---@return ItemStack
function Npc:barterOutcome(player, offer)
    if not offer or not self.barters then return nil end

    JLogF("barterOutcome start 3")

    -- Sort offer by templateID to make things simpler
    table.sort(offer, ItemStack.itemIDASC)

    -- Try to find a matching barter
    --- @type {to:ItemStack, from:ItemStack[]}
    local match = nil
    local count = nil
    for _, barter in ipairs(self.barters) do
        (function()
            local b = barter.from
            -- Wrong number of provided items
            if #b ~= #offer then
                JLogF("WRONG NUMBER OF ITEM {} / {}", #b, #offer)
                return
            end -- This return actually continues the for loop

            local tmpCount = nil
            for i in ipairs(b) do
                JLogF("Check item {}", i)
                -- Wrong item ID
                if b[i].itemID ~= offer[i].itemID then
                    JLogF("WRONG ITEM FOR {} {}, {}", i, b[i].itemID, offer[i].itemID)
                    return
                end
                -- Check quantity
                local bq, oq = b[i].quantity, offer[i].quantity

                JLogF("CHECK REMAINING COUNT FOR {} ", i)
                -- Remaining is not 0, wrong input
                if oq % bq ~= 0 then
                    JLogF("WRONG COUNT FOR {} {}/{}", i, oq, bq)
                    return
                end
                JLogF("CHECK STACK COUNT FOR {} ", i)
                local stackCount = oq/bq -- How many times can we barter with this stack

                JLogF("UPDATE TMP COUNT FOR {} ", i)
                if not tmpCount then
                    -- no tmpCount stored yet
                    tmpCount = stackCount
                    JLogF("Updating count to {}", tmpCount)
                elseif tmpCount ~= stackCount then
                    -- doesn't match stored size count
                    JLogF("DOESNT MATCH PREVIOUS SLOT COUNT FOR {} ({} / {})", i, tmpCount, stackCount)
                    return
                end
            end
            -- All itemIDs are good, we found a match
            match = barter
            count = tmpCount
            JLogF("Setting match/count {}/{}", match, count)
        end)()
        -- We found something, stop iteration
        if match then break end
    end
    JLogF("barterOutcome post loop {} x{}", match, count)

    --  No match
    if not match then
        JLogF("NO MATCH :(")
        return
    end
    JLogF("Found matching offer: {}({}) x {}", match.to.itemID, match.to.quantity, count)

    return {itemID= match.to.itemID, quantity= match.to.quantity*math.tointeger(count)}
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



---@class SaleOffer
---@field item:number
---@field price:number
---@field currency:number
SaleOffer = {}