local npc = Npc(558, 9032)

npc.gender = 1

npc.sales = {
    {item=577}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2342, {1948})
    elseif answer == 1948 then p:ask(9255, {9655})
    end
end

RegisterNPCDef(npc)
