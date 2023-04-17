-- MapDef class store data and logic for a specific map ID.
-- Most method will be called from the Java side with an Map (= map instance) as a first parameter.
---@class MapDef
---@field id number
---@field date string
---@field key string
---@field cellsData string
---@field width number
---@field height number
---@field x number
---@field y number
---@field subAreaId number
---@field capabilities number
---@field positions string
---@field npcs table<number, number[]>[] All Npcs that may appear on this map: <template,[cell,orientation]>
---@field mobGroupsCount number
---@field mobGroupsSize number
---@field staticGroups MobGroupDef[]
---@field allowedMobGrades table<number,number> K: MobTemplateID, V: Grade
---@field zaapCell number Only set for maps with a zaap. Value is cellId
---@field onMovementEnd table<number, function(md:MapDef, m:Map, p:Player)>
---@field onFightInit table<number, function(md:MapDef, m:Map,team1:Fighter[], team2:Fighter[])> K: fight type, V: Handler function
---@field onFightStart table<number, function(md:MapDef, m:Map,team1:Fighter[], team2:Fighter[])> K: fight type, V: Handler function
---@field onFightEnd table<number, function(md:MapDef, m:Map, winners:Fighter[], losers:Fighter[])> K: fight type, V: Handler function
---

-- Capabilities:
-- canChallenge (0x1)
-- canAttack (0x2)
-- canSaveTeleport (0x4)
-- canUseTeleport (0x8)
-- canUseInventory (0x10)
-- canUseObject (0x20)
-- canChangeCharacter (0x40)
-- canSell (0x80)
-- canCollectTax (0x100)
-- canSetPrism (0x200)

MAPS = {}

MapDef = {}
setmetatable(MapDef, {
    __call = function(cls, id, date, key, cellsData, width, height, x, y, subAreaId)
        local self = setmetatable({}, {
            __index = MapDef,
        })
        self.id = id
        self.date = date
        self.key = key
        self.cellsData = cellsData
        self.width = width
        self.height = height
        self.x = x
        self.y = y
        self.subAreaId = subAreaId
        self.capabilities = 0
        self.npcs = {}
        self.mobGroupsCount = 3
        self.mobGroupsSize = 8
        self.allowedMobGrades = {}
        self.positions = ""
        self.staticGroups = {}
        self.onMovementEnd = {}
        self.onFightInit = {}
        self.onFightStart = {}
        self.onFightEnd = {}
        self.zaapCell = nil

        MAPS[id] = self
        return self
    end,
})

---@param inst Map
function MapDef:update(inst) end


---@param mapId number
---@param cellId number
---@return function(MapDef, Map, Player)
function moveEndTeleport(mapId, cellId)
    ---@param p Player
    return function(md, m, p)
        p:teleport(mapId, cellId)
    end
end

function fightEndTeleportWinnerPlayers(mapId, cellId)
    return function(md, m, winners, losers)
        teleportPlayers(winners, mapId, cellId)
    end
end
