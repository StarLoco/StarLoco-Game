local npc = Npc(1162, 40)

npc.colors = {7536640, 3163940, 12967391}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    SmithDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
