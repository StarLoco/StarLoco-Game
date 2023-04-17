local npc = Npc(1038, 9037)

---@param p Player
function npc:onTalk(p, answer)
    IlyzaelleDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
