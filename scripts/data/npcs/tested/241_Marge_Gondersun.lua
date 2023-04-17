local npc = Npc(241, 9013)
--TODO: Lié à la quête Alignement Bonta #46
npc.gender = 1
npc.colors = {8205621, 16745574, 10632207}

npc.sales = {
	{item=491}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(935)
    end
end

RegisterNPCDef(npc)
