local npc = Npc(707, 9075)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    KitsouneDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
