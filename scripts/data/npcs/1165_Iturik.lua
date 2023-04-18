local npc = Npc(1165, 1265)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    BulbDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
