local npc = Npc(41, 9022)

npc.sales = {
    {item=496},
    {item=1332}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(103)
    end
end

RegisterNPCDef(npc)
