local npc = Npc(706, 1260)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    PandikazesDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
