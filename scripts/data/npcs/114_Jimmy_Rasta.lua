local npc = Npc(114, 9082)

npc.barters = {
    {to={itemID=803, quantity= 1}, from= {
        {itemID=290, quantity= 80},
        {itemID=288, quantity= 60}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(378)
    end
end

RegisterNPCDef(npc)
