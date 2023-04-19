local npc = Npc(780, 9032)

npc.gender = 1

---@param p Player
function npc:onTalk(p, answer)
    FieldDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
