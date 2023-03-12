local npc = Npc(464, 1207)
npc.sales = {
    {item=0x1ac9}
}

---@param player SPlayer
---@param answer number
function npc:onTalk(player, answer)
    local answers = player:kamas() >= 5 and {1662} or {}

    if answer == 0 then player:ask(1930, answers, "[name]")
    elseif answer == 1662 then
        if not player:tryBuyItem(0x1ac9, 5) then
            player:endDialog()
            return
        end
        player:ask(1931)
    end
end
RegisterNPCDef(npc)
