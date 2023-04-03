local npc = Npc(800, 1535)

---@param p Player
function npc:onTalk(p, answer)
    BrakmarRatDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)