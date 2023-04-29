local npc = Npc(226, 1046)
npc.accessories = {0, 1907, 0, 0, 0}

local nutId = 394

-- K: ItemTemplate / V: Dialog response
local itemResponses = {
    [nutId] = 1039, -- Nut
    [361] = 1040, -- Cawotte
    [1974] = 1041, -- Salace Leaf
    [290] = 1042, -- Mushroom
    [310] = 1043, -- Chafer bone
    -- 1044 is used for something unrelated
    [362] = 1045, -- Larva skin (blue)
    [363] = 1045, -- Larva skin (orange)
    [364] = 1045, -- Larva skin (green)
    [386] = 1046, -- Boar snout
    [365] = 1047, -- Arakne's legs
    [539] = 1048, -- Rye bread
}

local bookId = 2173
local buffId = 2128

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasBook = p:getItem(bookId)

    if answer == 0 then
        local responses = hasBook and {1038} or {}
        p:ask(1380, responses)
    elseif hasBook and answer == 1038 then
        local responseSet = {}
        -- Use a set to make sure we don't show duplicated responses
        for k, v in pairs(itemResponses) do
            if p:getItem(k) then
                responseSet[v] = true
            end
        end

        -- Set to list
        local responses = {}
        for k in pairs(responseSet) do
            table.insert(responses, k)
        end

        p:ask(1381, responses)
    elseif answer == itemResponses[nutId] and p:consumeItem(nutId, 1) then
        p:addItem(buffId, 1, RolePlayBuffSlot, true)
        p:ask(1384)
    else
        -- Check if the player want to give something else
        for k, v in pairs(itemResponses) do
            if answer == v and p:consumeItem(k, 1) then
                p:ask(1385)
                return
            end
        end
        p:endDialog()
    end
end

RegisterNPCDef(npc)
