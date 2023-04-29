local npc = Npc(758, 1439)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    SkeunkDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
