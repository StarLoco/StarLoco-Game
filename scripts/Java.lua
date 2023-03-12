--- DO NOT USE TYPES DEFINED HERE
--- THOSE ARE ONLY FOR AUTO-COMPLETION

---@class SPlayer
local SPlayer = {}

---@param question number
---@param answers table<number>
---@param param string
---@return void
function SPlayer:ask(question, answers,param) end

---@return void
function SPlayer:endDialog() end

---@param mapID number
---@param cellID number
---@return void
function SPlayer:teleport(mapID, cellID) end

---@return void
function SPlayer:openBank() end

---@param templateID number
---@param quantity number
---@return SItem
function SPlayer:getItem(templateID, quantity) end

---@param templateID number
---@param quantity number
---@return boolean
function SPlayer:consumeItem(templateID, quantity) end

---@param quantity number
---@return boolean
function SPlayer:modKamas(quantity) end

---@param templateID number
---@param quantity number
---@param isPerfect boolean defaults to true
---@return void
function SPlayer:addItem(templateID, quantity, isPerfect) end

---@param templateID number
---@param unitPrice number
---@param quantity number defaults to 1
---@param isPerfect boolean defaults to true
---@return boolean
function SPlayer:tryBuyItem(templateID, unitPrice, quantity, isPerfect) end


---@class SItem
local SItem = {}

---@param statID number
---@param val string
---@return boolean
function SItem:hasTxtStat(statID, val) end

---@param statID number
---@param val string
---@return boolean
function SItem:consumeTxtStat(statID, val) end
