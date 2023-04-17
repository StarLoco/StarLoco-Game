local npc = Npc(1163, 1047)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    SkeletonDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
