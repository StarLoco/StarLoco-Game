local npc = Npc(799, 1436)

---@param p Player
function npc:onTalk(p, answer)
    BontaRatDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)