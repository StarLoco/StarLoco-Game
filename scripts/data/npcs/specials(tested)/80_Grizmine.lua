local npc = Npc(80, 9025)

local changeGameResponse = 140
local changeGameResponse2 = 141

local pintsGame = {menuResponse = 121, answers = {}}

local lotteryGame = {menuResponse = 116, answers = {}}

local tgcGame = {menuResponse = 138, answers = {}}

local shiFuMiGame = {menuResponse = 1003, answers = {}}


--region Pints game

pintsGame.answers[121] = function(p)
    local responses = {}
    local kamas = p:kamas()

    if kamas >= 100 then
        table.insert(responses, 122)
    end
    if kamas >= 10 then
        table.insert(responses, 130)
    end
    table.insert(responses, changeGameResponse2)
    p:ask(144, responses)
end

local betResponse = function(qId, price, answers, randomResp)
    ---@param p Player
    return function(p)
        if not p:modKamas(-price) then
            p:endDialog()
        end
        p:setCtxVal("bet", price)
        local responses = table.shuffled(answers)
        table.insert(responses, randomResp) -- Choose randomly always last
        p:ask(qId, responses)
    end
end

pintsGame.answers[122] = betResponse(138, 100, {133, 134, 135}, 136)
pintsGame.answers[130] = betResponse(145, 10, {126, 127, 128}, 124)

local pintsResult = function(p)
    local bet = p:getCtxVal("bet")
    if not bet then
        -- WTF
        p:endDialog()
        return
    end

    -- 1/3 chance of winning
    local isWin = math.random(0, 3) == 0
    if not isWin then
        p:ask(164)
        return
    end
    p:modKamas(bet * 2)
    p:ask(140)
end

pintsGame.answers[124] = pintsResult
pintsGame.answers[126] = pintsResult
pintsGame.answers[127] = pintsResult
pintsGame.answers[128] = pintsResult
pintsGame.answers[133] = pintsResult
pintsGame.answers[134] = pintsResult
pintsGame.answers[135] = pintsResult
pintsGame.answers[136] = pintsResult

--endregion

--region Lottery

lotteryGame.answers[116] = function(p)
    local responses = {}
    if p:kamas() >= 10 then
        table.insert(responses, 118)
    end
    table.insert(responses, changeGameResponse)

    p:ask(135, responses)
end

-- Buy ticket
lotteryGame.answers[118] = function(p)
    if not p:modKamas(-10) then
        p:endDialog()
        return
    end
    p:ask(136, {131})
end

-- Ask if win
lotteryGame.answers[131] = function(p)
    local isWin = math.random(0, 5) == 0
    if not isWin then
        p:ask(164)
        return
    end
    p:modKamas(20000)
    p:ask(163)
end

--endregion

--region TCG

lotteryGame.answers[138] = function(p)
    p:ask(7918, {9636, 139})
end

lotteryGame.answers[139] = function(p)
    p:ask(7919)
end

lotteryGame.answers[9636] = function(p)
    local cell = p:cell()
    local orientation = p:orientation()

    if not (cell == 247 and orientation == 5) then
        p:ask(9649)
        return
    end

    p:ask(9640, {9638})
end

local function tcgTrapResponse(p)
    p:ask(9643, {9643})
end

lotteryGame.answers[9638] = function(p)
    p:ask(9642, {9641, 9639, 9640})
end

lotteryGame.answers[9639] = tcgTrapResponse
lotteryGame.answers[9640] = tcgTrapResponse
lotteryGame.answers[9641] = tcgTrapResponse

-- NPC Combo dialog
lotteryGame.answers[9643] = function(p)
    p:ask(9644, {9644})
end

-- Give up dialog
lotteryGame.answers[9644] = function(p)
    p:endDialog()
end

--endregion

--region Shi Fu Mi
-- Always win first
-- Random second hand
-- Lose third except when player has buff

local shiFuMiCheatBuffId = 2128

local shiFuMiPrice = 10
shiFuMiGame.answers[1003] = function(p)
    local responses = {}
    if p:kamas() >= shiFuMiPrice then
        table.insert(responses, 1004)
    end
    table.insert(responses, 1005)
    p:ask(1345, responses)
end

shiFuMiGame.answers[1004] = function(p)
    if not p:modKamas(-shiFuMiPrice) then
        p:endDialog()
        return
    end

    p:ask(1346, {1007, 1006, 1008})
end

shiFuMiGame.answers[1005] = function(p) p:ask(1347) end

shiFuMiGame.answers[1006] = function(p) p:ask(1348, {1010, 1011}) end

shiFuMiGame.answers[1007] = function(p) p:ask(1349, {1012, 1013}) end

shiFuMiGame.answers[1008] = function(p) p:ask(1350, {1014, 1015}) end

local shiFuMiSecondQuestion = function(p) p:ask(1351, {1017, 1016, 1018}) end

shiFuMiGame.answers[1010] = shiFuMiSecondQuestion

shiFuMiGame.answers[1011] = function(p) p:endDialog() end

shiFuMiGame.answers[1012] = shiFuMiSecondQuestion

shiFuMiGame.answers[1013] = function(p) p:endDialog() end

shiFuMiGame.answers[1014] = shiFuMiSecondQuestion

shiFuMiGame.answers[1015] = function(p) p:endDialog() end

---@param p Player
local shiFuMiSecondResult = function(winQId, winResponses, loseQId)
    return function(p)
        -- 1 out of 3 chances to win
        local isWin = math.random(0, 2) == 0

        if not isWin then
            p:ask(loseQId)
            return
        end

        p:ask(winQId, winResponses)
    end
end

shiFuMiGame.answers[1016] = shiFuMiSecondResult(1352, {1019, 1020}, 1355)
shiFuMiGame.answers[1017] = shiFuMiSecondResult(1353, {1021, 1023}, 1356)
shiFuMiGame.answers[1018] = shiFuMiSecondResult(1354, {1022, 1024}, 1357)


local shiFuMiThirdQuestion = function(p) p:ask(1358, {1025, 1026, 1027}) end

shiFuMiGame.answers[1019] = shiFuMiThirdQuestion
shiFuMiGame.answers[1020] = function(p) p:endDialog() end
shiFuMiGame.answers[1021] = shiFuMiThirdQuestion
shiFuMiGame.answers[1023] = function(p) p:endDialog() end
shiFuMiGame.answers[1022] = shiFuMiThirdQuestion
shiFuMiGame.answers[1024] = function(p) p:endDialog() end

---@param p Player
local shiFuMiThirdResult = function(winQId, winResponses, loseQId)
    ---@param p Player
    return function(p)
        local hasBuff = p:gearAt(RolePlayBuffSlot) and p:gearAt(RolePlayBuffSlot):id() == shiFuMiCheatBuffId

        if not hasBuff then
            p:ask(loseQId)
            return
        end

        p:ask(winQId, winResponses)
    end
end

shiFuMiGame.answers[1025] = shiFuMiThirdResult(1360, {1028}, 1355)
shiFuMiGame.answers[1026] = shiFuMiThirdResult(1362, {1029}, 1356)
shiFuMiGame.answers[1027] = shiFuMiThirdResult(1361, {1030}, 1357)

---@param p Player
local shiFuMiReward = function(p)
    p:learnEmote(11)
    p:learnEmote(12)
    p:learnEmote(13)

    p:ask(1366)
end

shiFuMiGame.answers[1028] = shiFuMiReward
shiFuMiGame.answers[1029] = shiFuMiReward
shiFuMiGame.answers[1030] = shiFuMiReward

--endregion

--region Menu / init

local games = {
    pintsGame,
    lotteryGame,
    tgcGame,
    shiFuMiGame,
}

---@param p Player
local function gameSelect(p, _)
    local responses = {}
    for _, v in ipairs(games) do
        table.insert(responses, v.menuResponse)
    end
    p:ask(134, responses)
end

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 or answer == changeGameResponse or answer == changeGameResponse2 then
        gameSelect(p)
        return
    end

    for _, game in ipairs(games) do
        if game.answers[answer] and type(game.answers[answer]) == "function" then
            game.answers[answer](p)
            return
        end
    end
    p:endDialog()
end
--endregion

RegisterNPCDef(npc)
