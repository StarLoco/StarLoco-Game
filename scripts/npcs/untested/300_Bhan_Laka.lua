local npc = Npc(300, 9008)

npc.sales = {
    {item=1866},
    {item=1865},
    {item=1864},
    {item=1867},
    {item=1863},
    {item=1862},
    {item=1868},
    {item=1860},
    {item=1861},
    {item=2111}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1167, {839, 838})
    elseif answer == 838 then p:ask(1168, {840})
    elseif answer == 840 then p:ask(1170)
    elseif answer == 839 then p:endDialog()
    end
end

RegisterNPCDef(npc)
