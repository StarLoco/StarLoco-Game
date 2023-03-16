local npc = Npc(694, 9067)

npc.colors = {12976128, 12976128, 0}

npc.sales = {
    {item=6987},
    {item=7007},
    {item=7332},
    {item=7004},
    {item=7008},
    {item=7333},
    {item=7005},
    {item=7009},
    {item=7334},
    {item=7006},
    {item=7331},
    {item=7335},
    {item=7336},
    {item=7337},
    {item=7338},
    {item=9474},
    {item=9475},
    {item=9476}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2880, {250})
    elseif answer == 250 then p:endDialog()
    end
end

RegisterNPCDef(npc)
