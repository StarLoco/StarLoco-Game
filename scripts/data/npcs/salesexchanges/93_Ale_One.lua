local npc = Npc(93, 9041)

npc.colors = {15385908, 7445844, 13098152}

npc.barters = {
    {to={itemID=287, quantity= 1}, from= {
        {itemID=432, quantity= 1}
    }}
}	

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(288, {229, 230})
	elseif answer == 229 then p:ask(289)
	elseif answer == 230 then p:ask(290)
    end
end

RegisterNPCDef(npc)
