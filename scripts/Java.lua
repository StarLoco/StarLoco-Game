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

---@param mapID number
---@param cellID number
---@return void
function Player:teleport(mapID, cellID) end

---@return void
function Player:openBank() end

---@param templateID number
---@param quantity number
---@return Item
function Player:getItem(templateID, quantity) end

---@param templateID number
---@param quantity number
---@return boolean
function Player:consumeItem(templateID, quantity) end

---@param quantity number
---@return boolean
function Player:modKamas(quantity) end

---@param templateID number
---@param quantity number
---@param isPerfect boolean defaults to true
---@param display boolean defaults to true
---@return void
function Player:addItem(templateID, quantity, isPerfect, display) end

---@param templateID number
---@param unitPrice number
---@param quantity number defaults to 1
---@param isPerfect boolean defaults to true
---@return boolean
function Player:tryBuyItem(templateID, unitPrice, quantity, isPerfect) end

---@return number
function Player:mapID() end
---@param actionID number
---@param actionType number
---@param actionVal string
---@return void
function Player:sendAction(actionID,actionType, actionVal) end


---@class Item
local Item = {}

---@param statID number
---@param val string
---@return boolean
function Item:hasTxtStat(statID, val) end

---@param statID number
---@param val string
---@return boolean
function Item:consumeTxtStat(statID, val) end