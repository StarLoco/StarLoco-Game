local npc = Npc(262, 9004)

npc.sales = {
    {item=946}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(946)
    end
end

RegisterNPCDef(npc)
