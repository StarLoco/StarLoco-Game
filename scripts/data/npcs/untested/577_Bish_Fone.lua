local npc = Npc(577, 9008)

npc.sales = {
    {item=1946}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1183, {852})
    elseif answer == 852 then p:ask(1184, {853})
    elseif answer == 853 then p:ask(1489)
    end
end

RegisterNPCDef(npc)
