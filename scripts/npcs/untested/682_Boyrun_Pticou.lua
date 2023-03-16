local npc = Npc(682, 120)

npc.accessories = {0, 0, 0, 0, 7073}

npc.sales = {
    {item=7045},
    {item=7047},
    {item=7046},
    {item=7055}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2844)
    end
end

RegisterNPCDef(npc)
