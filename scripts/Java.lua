--- DO NOT USE TYPES DEFINED HERE
--- THOSE ARE ONLY FOR AUTO-COMPLETION

---@param path string
function loadPack(path)  end

---@param fmt string
---@vararg any
function JLogF(fmt, ...)  end

---@param def Npc
function RegisterNPCDef(def)  end

---@param def MapDef
function RegisterMapDef(def)  end

---@param name string
---@param args string
---@param description string
function RegisterAdminCommand(name, args, description) end

---@param id number
---@param name string
---@param isPlayer boolean
---@param commands boolean|string[]
function RegisterAdminGroup(id, name, isPlayer, commands) end

---@param players number[]
---@param guilds number[]
---@param jobs number[]
---@param mounts number[]
---@param pvp number[]
---@param livitinems number[]
---@param tormentators number[]
---@param bandits number[]
function RegisterExpTables(players, guilds, jobs, mounts, pvp, livitinems, tormentators, bandits) end

---@param id number
---@param areaId number
---@param neighbors number[]
function RegisterSubArea(id, areaId, neighbors) end

---@param id number
---@param skills number[] defaults to {}
---@param walkable boolean defaults to false
function RegisterObjectDef(id, skills, walkable)  end

---@param t table<number, number>
function RegisterObjectForSprites(t)  end

---@class CellOverrides
---@field movement number

---@class KeyFrame
---@field frame number
---@field duration number (Optional) in ms
---@field next string (Optional) Next keyframe
---@field overrides CellOverrides


---@class Animation Java object holder for animation data. Lua should not need to access it

---@param spriteID number
---@param default string
---@param keyFrames table<string, KeyFrame>
---@return Animation
function RegisterAnimation(spriteID, default, keyFrames)  end

---@class Player
local Player = {}

---@return number
function Player:id() end

---@return string
function Player:name() end

---@return number
function Player:level() end

---@return number Player's Class
function Player:breed() end

---@return number
function Player:gender() end

---@return number
function Player:life() end

---@return number
function Player:maxLife() end

---@param life number
function Player:modLife(life) end

---@param percent number
function Player:setLifePercent(percent) end

---@return number
function Player:energy() end

--- Set player's energy, setting to 0 transforms it to tomb
---@param energy number
function Player:modEnergy(energy) end

---@return boolean
function Player:isGhost() end

---@return boolean
function Player:resurrect() end

---@param xp number
---@param show boolean
---@return boolean hasLeveledUp
function Player:addXP(xp, show) end

---@param question number
---@param answers table<number>
---@param param string
function Player:ask(question, answers, param) end

function Player:endDialog() end

function Player:pauseDialog() end

---@param map number
---@param cell number
function Player:teleport(map, cell) end

---@return void
function Player:openBank() end

---@param mapId number
---@param cellId number
---@param sendInfoMsg boolean defaults to false
---@return void
function Player:savePosition(mapId, cellId, sendInfoMsg) end

---@return void
function Player:openZaap() end

---@param cellID number
---@return void
function Player:openTrunk(cellID) end

---@param typeID number
---@return boolean worked
function Player:setExchangeAction(typeID) end

---@param typeID number -- expected typeID
---@return boolean worked
function Player:clearExchangeAction(typeID) end

---@param skillId number
---@param ingredientsCount number
---@return void
function Player:useCraftSkill(skillId, ingredientsCount) end

---@param template number
---@param quantity number
---@return Item
function Player:getItem(template, quantity) end

---@param template number
---@param quantity number
---@return boolean worked
function Player:consumeItem(template, quantity) end

---@param quantity number
---@return boolean worked
function Player:modKamas(quantity) end

---@return number,number used,max
function Player:pods() end

---@param pos number
---@return Item
function Player:gearAt(pos) end

---@param template number
---@param quantity number defaults to 1
---@param position number defaults to NotEquipped
---@param isPerfect boolean defaults to false
---@param display boolean defaults to true
---@return boolean worked
function Player:addItem(template, quantity, position, isPerfect, display) end

---@param template number
---@param unitPrice number
---@param quantity number defaults to 1
---@param isPerfect boolean defaults to true
---@return boolean worked
function Player:tryBuyItem(template, unitPrice, quantity, isPerfect) end

---@return number
function Player:mapID() end

---@return Map
function Player:map() end

---@return number
function Player:cell() end

---@return number
function Player:orientation() end

---@return number,number map,cell
function Player:savedPosition() end

---@param mapId number
---@param cellId number
---@param sendIm boolean defaults to true
function Player:setSavedPosition(mapId, cellId, sendIm) end

---@param emote number
---@return boolean
function Player:hasEmote(emote) end

---@param emote number
---@return boolean worked
function Player:learnEmote(emote) end

---@param id number
---@param type number
---@param val string
---@return void
function Player:sendAction(id,type, val) end

---@param type number
---@param id number
---@return void
function Player:sendInfoMsg(type, id) end

---@return number[] job IDs
function Player:jobs() end

---@param job number
---@return number -- 0 if not known
function Player:jobLevel(job) end

---@param job number
---@return boolean worked
function Player:canLearnJob(job) end

---@param job number
---@return boolean worked
function Player:tryLearnJob(job) end

---@param job number
---@return boolean worked
function Player:tryUnlearnJob(job) end

---@param job number
---@param xp number
---@return boolean worked
function Player:addJobXP(job, xp) end

---@param spell number
---@return number -- 0 if not known
function Player:spellLevel(spell) end

---@param spell number
---@param level number 0 to unlearn, works even if spell is not known yet
---@param modPoints boolean Do we spend/grant spell points
---@return boolean true if player's spell is at the expected level after the call
function Player:setSpellLevel(spell, level, modPoints) end

function Player:spellResetPanel() end

---@param faction number
---@param replace boolean defaults to false
---@return boolean true if player's faction has the expected value after the call
function Player:setFaction(faction, replace) end

---@param cellID number
---@param def MobGroupDef
function Player:forceFight(cellID, def) end

---@param mapId number
function Player:compassTo(mapId) end

--- Stores a value in current exchange action
--- This is NOT saved in database and will be lost when the exchange action is over
---@param key string
---@param value any
---@return boolean
function Player:setCtxVal(key, value) end

--- Gets a value stored via setCtxVal
---@param key string
---@return any
function Player:getCtxVal(key) end

--- Start a scripted scenario
---@param id number
---@param date string
---@param onEnd fun(p:Player,succeed:boolean)
function Player:startScenario(id, date, onEnd) end

---@param id number
---@param date string
function Player:openDocument(id, date) end

---@param actorID number
---@param quantity number
function Player:showReceivedItem(actorID, quantity) end

---@param effectID number
---@return number
function Player:baseStat(effectID) end

---@param effectID number
---@param value number
---@return number -- new value
function Player:modScrollStat(effectID, value) end

---@param includeScrolls boolean defaults to false
function Player:resetStats(includeScrolls) end

--endregion

---@class MobGroupDef table<number,number[]> K: MobID, V: grades
---@class MobGroupSpawnDef table<number, MobGroupDef[]> K: CellID, V: group

---@class Item
local Item = {}

--- Returns the guid
---@return number
function Item:guid() end

--- Returns the template id
---@return number
function Item:id() end

--- Returns the template type id
---@return number
function Item:type() end

---@param stat number
---@param val string
---@return boolean
function Item:hasTxtStat(stat, val) end

---@param stat number
---@param val string
---@return boolean
function Item:consumeTxtStat(stat, val) end

---@param stat number
---@return number timestamp millis
function Item:dateStatTS(stat) end

---@class Account
local Account = {}

---@return number
function Account:id() end

---@return number[] accountId[]
function Account:friends() end

---@class World
local World = {}

---@return {day:number, month:number,year:number,hour:number,min:number,sec:number}
function World:datetime() end

---@return number timestamp millis
function World:clock() end

---@param nameOrId string|number
---@return Account
function World:account(nameOrId) end

---@param nameOrId string|number
---@return Player
function World:player(nameOrId) end

---@param id number
---@return SubArea
function World:subArea(id) end

---@param id number
---@return Map
function World:map(id) end

---@param delay number in milliseconds
---@param fn fun
function World:delayForMs(delay, fn) end

-- Map instance
---@class Map
local Map = {}

---@return number
function Map:id() end

---@return MapDef
function Map:def() end

---@return Area
function Map:area() end

---@return SubArea
function Map:subArea() end

---@param cellId number
---@return Player[]
function Map:cellPlayers(cellId) end

---@param actorId number
---@return MobGrade[][]
function Map:mobGroupById(actorId) end

---@return MobGrade[][]
function Map:mobGroups() end

---@param def MobGroupDef
---@return number actorId
function Map:spawnGroupDef(def) end

--- updateNpcExtraForPlayer sends a GX packet to the player
---@param npcDefId number
---@param player Player
function Map:updateNpcExtraForPlayer(npcDefId, player) end

--- getAnimationState returns the cell animation state
---@param cellId number
---@return string
function Map:getAnimationState(cellId) end

--- setAnimationState sets a cell animation state
---@param cellId number
---@param name string
function Map:setAnimationState(cellId, name) end

--- setCellData sets a cell data override field
---@param cellId number
---@param field string
---@param val number
function Map:setCellData(cellId, field, val) end

--- resetCellData resets a cell data override field
---@param cellId number
---@param field string
function Map:resetCellData(cellId, field) end

---@param p Player
---@param id number
---@param type number
---@param val string
function Map:sendAction(p, id, type, val) end

-- Area
---@class Area
local Area = {}

---@return number
function Area:id() end

-- SubArea
---@class SubArea
local SubArea = {}

---@return number
function SubArea:id() end

---@return Area
function SubArea:area() end

---@return number
function SubArea:faction() end

---@return boolean
function SubArea:conquerable() end


-- MobGrade
---@class MobGrade
local MobGrade = {}

---@return number
function MobGrade:id() end

---@return number
function MobGrade:grade() end

---@return number
function MobGrade:level() end

-- Fighter
---@class Fighter =(Player|MobGrade)