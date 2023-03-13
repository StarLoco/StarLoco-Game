--- DO NOT USE TYPES DEFINED HERE
--- THOSE ARE ONLY FOR AUTO-COMPLETION

---@class Player
local Player = {}

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

---@param template number
---@param quantity number
---@param isPerfect boolean defaults to true
---@param display boolean defaults to true
---@return void
function Player:addItem(template, quantity, isPerfect, display) end

---@param template number
---@param unitPrice number
---@param quantity number defaults to 1
---@param isPerfect boolean defaults to true
---@return boolean worked
function Player:tryBuyItem(template, unitPrice, quantity, isPerfect) end

---@return number
function Player:mapID() end

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

---@param job number
---@return number -- 0 if not known
function Player:jobLevel(job) end

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

---@class Item
local Item = {}

---@param stat number
---@param val string
---@return boolean
function Item:hasTxtStat(stat, val) end

---@param stat number
---@param val string
---@return boolean
function Item:consumeTxtStat(stat, val) end