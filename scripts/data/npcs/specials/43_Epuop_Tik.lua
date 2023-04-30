local npc = Npc(43, 9029)
--TODO: NPC Temple Sadi > Check dialogs offi en étant Sadi
npc.sales = {
    {item=789},
    {item=1344},
    {item=498}

}

npc.barters = {
    {to={itemID=498, quantity= 1}, from= {
        {itemID=304, quantity= 1}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(92)
    end
end

RegisterNPCDef(npc)
