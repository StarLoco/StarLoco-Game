local npc = Npc(63, 9037)

npc.sales = {
    {item=498},
    {item=499},
    {item=500}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(37, {275})
    elseif answer == 275 then p:ask(341, {350, 351, 352})
    end
end

RegisterNPCDef(npc)