package.path = package.path .. ";./scripts/?.lua;"


---@param n number
---@return string
function hex(n)
    return string.format("%x", n)
end


---@class ItemStack
---@field itemID:number
---@field quantity:number
local ItemStack = {}
