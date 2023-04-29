local npc = Npc(98, 9033)

npc.gender = 1

npc.barters = {
    {to={itemID=678, quantity= 1}, from= {
        {itemID=307, quantity= 10}
    }}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(311, {254, 252, 253})
    elseif answer == 254 then p:ask(314)
	elseif answer == 252 then p:ask(312, {255, 256})
	elseif answer == 255 then p:ask(315)
	elseif answer == 256 then p:ask(316)
	elseif answer == 253 then p:ask(313, {257, 258})
	elseif answer == 257 then p:ask(316)
	elseif answer == 258 then p:ask(315)
    end
end

RegisterNPCDef(npc)
