local npc = Npc(1284, 1728)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    QuTanDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
