local npc = Npc(384, 9068)

npc.sales = {
    {item=1336}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1609, {1241})
    elseif answer == 1241 then p:endDialog()
    end
end

RegisterNPCDef(npc)
