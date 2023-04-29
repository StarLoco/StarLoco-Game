local npc = Npc(102, 9041)

npc.barters = {
    {to={itemID=720, quantity= 1}, from= {
        {itemID=371, quantity= 120},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(345)
    end
end

RegisterNPCDef(npc)
