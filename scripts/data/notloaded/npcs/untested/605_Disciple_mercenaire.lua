local npc = Npc(605, 8007)

npc.gender = 1

npc.sales = {
    {item=940}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2449)
    end
end

RegisterNPCDef(npc)
