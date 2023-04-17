local npc = Npc(95, 9041)

npc.colors = {10713946, 13158601, 5600876}

npc.barters = {
    {to={itemID=582, quantity= 1}, from= {
        {itemID=2241, quantity= 1},
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(295)
    end
end

RegisterNPCDef(npc)
