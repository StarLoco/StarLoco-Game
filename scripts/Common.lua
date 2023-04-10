package.path = package.path .. ";./scripts/?.lua;"

---@param n number
---@return string
function hex(n)
    return string.format("%x", n)
end

---@class ItemStack
---@field itemID:number (0 for kamas)
---@field quantity:number
ItemStack = {}

-- Helper for sorting stacks
---@param other ItemStack
---@return boolean self < other
function ItemStack.itemIDASC(self, other)
    return self.itemID < other.itemID
end


---@param players Player[]
---@param mapId number
---@param cellId number
function teleportPlayers(players, mapId, cellId)
    for _, player in ipairs(players) do
        -- This ensure the player entity can be teleported
        if player.teleport then
            player:teleport(mapId, cellId)
        end
    end
end