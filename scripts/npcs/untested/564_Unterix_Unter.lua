local npc = Npc(564, 9003)

npc.sales = {
    {item=1934},
    {item=1936},
    {item=1938},
    {item=1939},
    {item=1940},
    {item=1941},
    {item=1942},
    {item=1943}
}

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
