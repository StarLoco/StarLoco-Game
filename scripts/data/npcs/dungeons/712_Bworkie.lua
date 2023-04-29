local npc = Npc(712, 1243)

---@param p Player
function npc:onTalk(p, answer)
    BworkerDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
