local npc = Npc(771, 9078)

npc.sales = {
    {item=597},
    {item=7799}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3160, {}, "[name]")
    end
end

RegisterNPCDef(npc)
