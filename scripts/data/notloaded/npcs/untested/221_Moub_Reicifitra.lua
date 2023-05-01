local npc = Npc(221, 9015)

npc.sales = {
    {item=6601}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(936)
    end
end

RegisterNPCDef(npc)
