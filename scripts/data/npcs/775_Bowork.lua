local npc = Npc(775, 1003)

---@param p Player
function npc:onTalk(p, answer)
    BworkDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)