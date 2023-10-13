local npc = Npc(564, 9003)

---@param p Player
function npc:salesList(p)
    local jobLvl = p:jobLevel(HunterJob)
    local sales = {}
    
    if jobLvl >= 1  then table.insert(sales, {item=1934}) end
    if jobLvl >= 10 then table.insert(sales, {item=1936}) end
    if jobLvl >= 30 then table.insert(sales, {item=1938}) end
    if jobLvl >= 30 then table.insert(sales, {item=1941}) end
    if jobLvl >= 30 then table.insert(sales, {item=1942}) end
    if jobLvl >= 30 then table.insert(sales, {item=1943}) end
    if jobLvl >= 50 then table.insert(sales, {item=1939}) end
    if jobLvl >= 70 then table.insert(sales, {item=1940}) end

    return sales
end

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2357, {1962, 1967, 1968})
    elseif answer == 1962 then p:ask(2358, {1963})
    elseif answer == 1963 then p:ask(2359, {1964})
    elseif answer == 1964 then p:ask(2361, {1965})
    elseif answer == 1965 then p:ask(2362, {1966})
    end
end

RegisterNPCDef(npc)
