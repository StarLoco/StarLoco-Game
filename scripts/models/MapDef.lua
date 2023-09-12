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
---@field mobGroupsMinSize number
---@field mobGroupsMaxSize number
---@field staticGroups MobGroupDef[]
---@field allowedMobGrades table<number,number> K: MobTemplateID, V: Grade
---@field zaapCell number Only set for maps with a zaap. Value is cellId
---@field onMovementEnd table<number, fun(md:MapDef, m:Map, p:Player)>
---@field onFightInit table<number, fun(md:MapDef, m:Map,team1:Fighter[], team2:Fighter[])> K: fight type, V: Handler function
---@field onFightStart table<number, fun(md:MapDef, m:Map,team1:Fighter[], team2:Fighter[])> K: fight type, V: Handler function
---@field onFightEnd table<number, fun(md:MapDef, m:Map, winners:Fighter[], losers:Fighter[])> K: fight type, V: Handler function
---@field objects table<number, InteractiveObjectDef|fun(p:Player, skillId:number):boolean> K: cellId, V: InteractiveObjectDef or handler function
---@field animations table<number, AnimatedObject>
---@field switches table<number, fun(md:MapDef, p:Player)>
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

---@type table<number, MapDef>
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
        self.mobGroupsMinSize = 1
        self.mobGroupsMaxSize = 8
        self.allowedMobGrades = {}
        self.positions = ""
        self.staticGroups = {}
        self.onMovementEnd = {}
        self.onFightInit = {}
        self.onFightStart = {}
        self.onFightEnd = {}
        self.objects = {}
        self.switches = {}
        self.animations = {}
        self.zaapCell = nil

        if MAPS[id] then
            error(string.format("map #%d already registered", self.id))
        end

        MAPS[id] = self
        return self
    end,
})

---@param p Player
---@param cellId number
---@param skillId number
---@return boolean worked
function MapDef:onObjectUse(p, cellId, skillId)
    local object = self.objects[cellId]

    if not object then return false end

    if type(object) == "function" then
        return object(p, skillId)
    end

    return object:onUseSkill(p, cellId, skillId)
end

---@param docId number
---@param docDate string
---@return fun(p:Player, skillId:number):boolean
function onObjectUseOpenDocument(docId, docDate)
    ---@param p Player
    return function(p, skillId)
        if skillId ~= 0 then return false end

        p:openDocument(docId, docDate)
        return true
    end
end

---@return fun(p:Player, skillId:number):boolean
function onObjectUseResurrect()
    ---@param p Player
    return function(p, skillId)
        print(p:name(), skillId)
        if skillId ~= 0 then return false end
        return p:resurrect()
    end
end

---@param mapId number
---@param cellId number
---@return fun(MapDef, Map, Player)
function moveEndTeleport(mapId, cellId)
    ---@param p Player
    return function(md, m, p)
        p:teleport(mapId, cellId)
    end
end

function fightEndTeleportWinnerPlayer(mapId, cellId)
    return function(p, isWinner, _, _)
        if not isWinner then return end
        p:teleport(mapId, cellId)
    end
end

function openAndCloseAfterMillis(mapId, cellId, delayMs)
    return function(md, m, p)
        JLogF("{} is opening door #{} for {} ms", p:name(), cellId, delayMs)

    end
end