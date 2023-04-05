local npc = Npc(758, 1439)

---@param p Player
function npc:onTalk(p, answer)
    SkeunkDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)