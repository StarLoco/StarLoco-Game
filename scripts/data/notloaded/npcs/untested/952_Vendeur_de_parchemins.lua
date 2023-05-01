local npc = Npc(952, 1155)

npc.sales = {
    {item=810},
    {item=805},
    {item=797},
    {item=814},
    {item=817},
    {item=801},
    {item=684},
    {item=718},
    {item=720},
    {item=721},
    {item=9200},
    {item=719},
    {item=9201}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4225, {3706, 3707, 3708})
    elseif answer == 3706 then p:endDialog()
    elseif answer == 3707 then p:endDialog()
    elseif answer == 3708 then p:endDialog()
    end
end

RegisterNPCDef(npc)
