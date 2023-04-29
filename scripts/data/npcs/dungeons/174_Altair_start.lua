local npc = Npc(174, 9059)

npc.colors = {6380875, 16777215, 11250604}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    GobballDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
