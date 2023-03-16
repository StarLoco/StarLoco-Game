local npc = Npc(59, 9035)

npc.gender = 1

npc.sales = {
    {item=454}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(33, {279, 281})
    elseif answer == 279 then p:ask(344, {280})
    elseif answer == 281 then p:ask(346, {287, 369, 286, 288})
    end
end

RegisterNPCDef(npc)