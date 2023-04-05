package.path = package.path .. ";./scripts/?.lua;"

---@param n number
---@return string
function hex(n)
    return string.format("%x", n)
end

---@class ItemStack
---@field itemID:number
---@field quantity:number
ItemStack = {}

-- Helper for sorting stacks
---@param other ItemStack
---@return boolean self < other
function ItemStack.itemIDASC(self, other)
    return self.itemID < other.itemID
end
