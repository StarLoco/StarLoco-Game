local npc = Npc(464, 1207)
npc.sales = {
    {item=6857},
    {item=311},
    {item=6765}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local answers = p:kamas() >= 5 and {1662} or {}

    if answer == 0 then p:ask(1930, answers, "[name]")
    elseif answer == 1662 then
        if not p:tryBuyItem(6857, 5) then
            p:endDialog()
            return
        end
        p:ask(1931)
    end
end

RegisterNPCDef(npc)
