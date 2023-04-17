--- DO NOT USE TYPES DEFINED HERE
--- THOSE ARE ONLY FOR AUTO-COMPLETION

---@param path string
function loadDir(path)  end

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

---@return boolean
function Player:isGhost() end

---@param xp number
---@return boolean hasLeveledUp
function Player:addXP(xp) end

---@param question number
---@param answers table<number>
---@param param string
---@return void
function Player:ask(question, answers, param) end

---@return void
function Player:endDialog() end

---@param map number
---@param cell number
---@return void
function Player:teleport(map, cell) end

---@return void
function Player:openBank() end

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

---@param job number
---@return number -- 0 if not known
function Player:jobLevel(job) end

---@param job number
---@return boolean worked
function Player:tryLearnJob(job) end

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

---@param faction number
---@param replace boolean defaults to false
---@return boolean true if player's faction has the expected value after the call
function Player:setFaction(faction, replace) end

--region Quests
---@param quest number
---@return boolean
function Player:questAvailable(quest) end

---@param quest number
---@return boolean
function Player:questFinished(quest) end

---@param quest number
---@return boolean
function Player:questOngoing(quest) end

---@param quest number
---@return boolean
function Player:startQuest(quest) end

---@param quest number
---@return boolean
function Player:completeObjective(quest, objective) end

---@param def MobGroupDef {cellId, {mobID,grade}[]}
function Player:forceFight(def) end

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

--endregion

---@class MobGroupDef table<number, table<number,number[]>[]>


---@class Item
local Item = {}

--- Returns the guid
---@return number
function Item:guid() end

--- Returns the template id
---@return number
function Item:id() end

---@param stat number
---@param val string
---@return boolean
function Item:hasTxtStat(stat, val) end

---@param stat number
---@param val string
---@return boolean
function Item:consumeTxtStat(stat, val) end

---@class World
local World = {}

---@return {day:number, month:number,year:number,hour:number,min:number,sec:number}
function World:time() end

---@param nameOrId string|number
---@return Player
function World:player(nameOrId) end

---@param id number
---@return SubArea
function World:subArea(id) end

---@param id number
---@return Map
function World:map(id) end

-- Map instance
---@class Map
local Map = {}

---@return number
function Map:id() end

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

--- updateNpcForPlayer sends a GM|~ packet to the player
---@param player Player
---@param npc Npc
function Map:updateNpcForPlayer(player, npc) end

-- SubArea
---@class SubArea
local SubArea = {}

---@return number
function SubArea:id() end

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