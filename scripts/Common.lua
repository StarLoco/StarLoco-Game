package.path = package.path .. ";./scripts/?.lua;"

---@param n number
---@return string
function hex(n)
    return string.format("%x", n)
end

table.contains = function(t, value)
    for _, v in ipairs(t) do
        if v == value then
            return true
        end
    end
    return false
end

table.shuffled = function(t)
    local out = {}
    for i, v in ipairs(t) do
        local pos = math.random(1, #out+1)
        table.insert(out, pos, v)
    end
    return out
end

---@param t table
---@param fn fun(e:any):boolean
---@return boolean
table.ifind = function(t, fn)
    for _, v in ipairs(t) do
        if fn(v) then
            return v
        end
    end
end


function requireReload(path)
    package.loaded[path] = nil
    require(path)
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

---@param fighters Fighter[]
---@param id number
---@return number
function countFightersForMobId(fighters, id)
    local count = 0
    for _, mob in ipairs(fighters) do
        if mob.grade and mob:id() == id then
            count = count + 1
        end
    end
    return count
end